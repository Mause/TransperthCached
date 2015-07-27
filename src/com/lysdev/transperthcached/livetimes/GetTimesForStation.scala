package com.lysdev.transperthcached.livetimes

import android.util.Log

import java.util.ArrayList
import java.util.HashMap
import java.util.List

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

object GetTimesForStation {
    def getTimes(station_name: String) : TimesForStation = {
        val doc = Util.getXML(
            "GetSercoTimesForStation", Seq("stationname" -> station_name).toMap
        )
        if (doc == null) {
            Log.d("TransperthCached", "Bad document")
            return null
        }

        val wrapper = new GetWrapper(doc.getDocumentElement())

        val s_last_update = wrapper.get("LastUpdate")
        val dt = ISODateTimeFormat.dateHourMinuteSecond().parseDateTime(
            s_last_update
        )

        val nodeList = (
            doc.getElementsByTagName("Trips")
            .item(0)
            .asInstanceOf[Element]
            .getElementsByTagName("SercoTrip")
        )

        val tripList = new NodeListIterator(nodeList).asScala.toSeq.map(Trip.fromNode(_))

        return new TimesForStation(
            wrapper.get("Name"),
            dt,
            tripList
        )
    }
}
