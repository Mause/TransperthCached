package com.lysdev.transperthcached.silverrails

import android.net.Uri
import android.util.Log

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.ArrayList

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import com.lysdev.transperthcached.utils.Util

import scala.util.Try


object GetNearbyTransitStops {
    def getNearby(apikey: String, loco: String) : List[NearbyTransitStop] = {
        val latlon = loco.split(",").map(_.trim)
        getNearby(apikey, latlon(0), latlon(1))
    }

    def getNearby(apikey: String, latitude: String, longitude: String) : List[NearbyTransitStop] = {
        getNearby(
            apikey,
            parseDouble(latitude),
            parseDouble(longitude)
        )
    }

    def getNearby(apikey: String, latitude: Double, longitude: Double) : List[NearbyTransitStop] = {
        var url = Constants.BASE_URL + "Datasets/PerthRestricted/NearbyTransitStops"

        url = Uri.parse(url).buildUpon()
            .appendQueryParameter("ApiKey", apikey)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("GeoCoordinate", f"$latitude,$longitude")
            .build().toString()

        val url_obj = new URL(url)

        var conn : URLConnection = null
        try {
            conn = url_obj.openConnection()
        } catch {
            case e: IOException => {
                Log.e("TransperthCached", "Couldn't open connection!", e)
                throw new java.lang.Error("Couldn't open connection!")
            }
        }

        var raw_json : String = ""
        try {
            raw_json = Util.convertStreamToString(conn.getInputStream())
        } catch {
            case e: IOException => Log.e("TransperthCached", "Couldn't convert stream to string", e)
        }

        try {
            parseJSON(raw_json)
        } catch {
            case e: JSONException => {
                Log.e("TransperthCached", "Couldn't parse JSON", e)
                List[NearbyTransitStop]()
            }
        }
    }

    def parseDouble(s: String) : Double = {
        Try(s.toDouble).toOption match {
            case None    => 0
            case Some(x) => x
        }
    }

    def parseIndividual(obj: JSONObject) : NearbyTransitStop = {
        val distance = obj.getInt("Distance")
        val transitStop = obj.getJSONObject("TransitStop")
        val location_parts = (
            transitStop
            .getString("Position")
            .split(", ")
            .map(_.trim)
            .map(parseDouble)
        )

        new NearbyTransitStop(
            distance,

            location_parts(0),
            location_parts(1),
            transitStop.getString("Description"),
            transitStop.getInt("Code")
        )
    }

    def parseJSON(raw_json: String) : List[NearbyTransitStop] = {
        val json = new JSONObject(raw_json)
        val stop_paths = json.optJSONArray("TransitStopPaths")

        if (stop_paths == null) {
            List()

        } else {
            (
                for (i <- 0 until stop_paths.length())
                    yield parseIndividual(stop_paths.getJSONObject(i))
            ).toList
        }
    }
}
