package com.lysdev.transperthcached.livetimes

import android.util.Log

import org.joda.time.format.ISODateTimeFormat
import scala.collection.JavaConverters._

object GetTimesForStation {
    def getTimes(station_name: String) : TimesForStation = {
        val doc = Util.getXML(
            "GetSercoTimesForStation", Seq("stationname" -> station_name).toMap
        )
        if (doc == null) {
            Log.d("TransperthCached", "Bad document")
            return null
        }

        val s_last_update = (doc \ "LastUpdate").text
        val dt = ISODateTimeFormat.dateHourMinuteSecond().parseDateTime(
            s_last_update
        )

        val tripList = (
            (doc \ "Trips" \ "SercoTrip")
            .map(Trip.fromRaw(_))
            .toList
            .asJava
        )

        return new TimesForStation(
            (doc \ "Name").text,
            dt,
            tripList
        )
    }
}
