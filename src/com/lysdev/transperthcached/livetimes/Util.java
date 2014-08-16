package com.lysdev.transperthcached.livetimes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import android.net.Uri;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Util {
    public static Document getXML(String url) {
        return getXML(url, new HashMap<String,String>());
    }

    public static Document getXML(String raw_url, Map<String,String> queryParams) {
        Document doc = null;
        DocumentBuilder db = null;
        URL url = null;

        Uri.Builder uri = Uri.parse(Constants.BASE_URL + raw_url).buildUpon();
        for (int i=0; i<(queryParams.size() / 2); i+=2) {
            uri.appendQueryParameter(queryParams.get(i), queryParams.get(i+1));
        }

        try {
            url = new URL(uri.build().toString());
        } catch (MalformedURLException mue) {
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            return null;
        }

        try {
            doc = db.parse(new InputSource(url.openStream()));
        } catch (SAXException se) {
            return null;
        } catch (IOException ioe) {
            return null;
        }

        doc.getDocumentElement().normalize();

        return doc;
    }
}
