package com.lysdev.transperthcached.livetimes

import android.net.Uri
import android.util.Log

import scala.collection.JavaConverters._
import scala.xml.{XML,Elem}


object Util {
    def getXML(raw_url: String) : Elem = {
        getXML(raw_url, Map[String,String]())
    }

    def getXML(raw_url: String, queryParams: java.util.Map[String, String]) : Elem = {
        val params = (
            queryParams
            .asScala
            .asInstanceOf[
                scala.collection.immutable.Map[String,String]
            ]
        )
        getXML(raw_url, params)
    }

    def getXML(raw_url: String, queryParams: Map[String, String]) : Elem = {
        val uri = Uri.parse(Constants.BASE_URL + raw_url).buildUpon()
        queryParams.foreach((tup) => uri.appendQueryParameter(tup._1, tup._2))

        Log.d("TransperthCached", String.format("URL: %s", uri.build().toString()))

        XML.load(uri.build().toString())
    }
}
