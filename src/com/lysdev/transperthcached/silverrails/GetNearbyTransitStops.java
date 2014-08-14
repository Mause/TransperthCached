package com.lysdev.transperthcached.silverrails;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lysdev.transperthcached.Util;
import com.lysdev.transperthcached.silverrails.Constants;


public class GetNearbyTransitStops {
    public static ArrayList<NearbyTransitStop> getNearby(String apikey, String loco) {
        String[] parts = loco.split(",");
        return getNearby(apikey, parts[0].trim(), parts[1].trim());
    }

    public static ArrayList<NearbyTransitStop> getNearby(
            String apikey, String latitude, String longitude) {
        return getNearby(
            apikey,
            Double.parseDouble(latitude),
            Double.parseDouble(longitude)
        );
    }

    public static ArrayList<NearbyTransitStop> getNearby(
            String apikey, double latitude, double longitude) {

        String url = Constants.BASE_URL + "Datasets/PerthRestricted/NearbyTransitStops";

        url = Uri.parse(url).buildUpon()
            .appendQueryParameter("ApiKey", apikey)
            .appendQueryParameter("format", "json")
            .appendQueryParameter(
                "GeoCoordinate", String.format("%f,%f", latitude, longitude)
            )
            .build().toString();

        URL url_obj = null;
        try {
            url_obj = new URL(url);
        } catch (MalformedURLException e) {
            // why java? why do you force me to handle every url, however
            // unlikely their occurance?
            Log.e("TransperthCached", "bad url", e);
        }

        URLConnection conn;
        try {
            conn = url_obj.openConnection();
        } catch (IOException e) {
            Log.e("TransperthCached", "Couldn't open connection!", e);
            throw new java.lang.Error("Couldn't open connection!");
        }

        // ?ApiKey=eac7a147-0831-4fcf-8fa8-a5e8ffcfa039
        // GeoCoordinate=-32.0102024,115.8853261

        String raw_json = "";
        try {
            raw_json = Util.convertStreamToString(conn.getInputStream());
        } catch (IOException e) {
            Log.e("TransperthCached", "Couldn't convert stream to string", e);
        }

        try {
            return parseJSON(raw_json);
        } catch (JSONException e) {
            Log.e("TransperthCached", "Couldn't parse JSON", e);
            return new ArrayList<NearbyTransitStop>();
        }
    }

    private static ArrayList<NearbyTransitStop> parseJSON(String raw_json) throws JSONException {
        JSONObject json = new JSONObject(raw_json);

        JSONArray stop_paths = json.optJSONArray("TransitStopPaths");
        if (stop_paths == null) return null;

        ArrayList<NearbyTransitStop> parsed = new ArrayList<NearbyTransitStop>();
        for (int i = 0; i < stop_paths.length(); i++) {
            JSONObject cur = stop_paths.getJSONObject(i);

            Integer distance = cur.getInt("Distance");
            JSONObject transitStop = cur.getJSONObject("TransitStop");

            String[] location_parts = transitStop.getString("Position").split(", ");

            // Iterator<String> keys = cur.keys();
            // while (keys.hasNext()) {
            //     Log.d("TransperthCached", "Key; " + keys.next());
            // }

            parsed.add(
                new NearbyTransitStop(
                    distance,

                    Double.parseDouble(location_parts[0].trim()),
                    Double.parseDouble(location_parts[1].trim()),
                    transitStop.getString("Description"),
                    Integer.parseInt(transitStop.getString("Code"))
                )
            );
        }

        return parsed;
    }
}
