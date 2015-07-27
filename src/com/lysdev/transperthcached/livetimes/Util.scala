package com.lysdev.transperthcached.livetimes

import android.net.Uri
import android.util.Log

import java.net.URL

import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.xml.sax.InputSource

import scala.collection.JavaConverters._


object Util {
    def getXML(raw_url: String) : Document = {
        getXML(raw_url, Map[String,String]())
    }

    def getXML(raw_url: String, queryParams: java.util.Map[String, String]) : Document = {
        getXML(raw_url, queryParams.asScala.asInstanceOf[scala.collection.immutable.Map[String,String]])
    }

    def getXML(raw_url: String, queryParams: Map[String, String]) : Document = {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()

        val uri = Uri.parse(Constants.BASE_URL + raw_url).buildUpon()
        queryParams.foreach((tup) => uri.appendQueryParameter(tup._1, tup._2))

        Log.d("TransperthCached", String.format("URL: %s", uri.build().toString()))

        val url = new URL(uri.build().toString())
        val doc = db.parse(new InputSource(url.openStream()))

        doc.getDocumentElement().normalize()

        doc
    }
}
