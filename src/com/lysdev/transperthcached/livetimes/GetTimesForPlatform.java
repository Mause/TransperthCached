package com.lysdev.transperthcached.livetimes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;

public class GetTimesForPlatform {

    private static boolean isValidCode(String code) {
        Pattern patt = Pattern.compile("[A-Z]{3}\\d");

        Matcher match = patt.matcher(code);

        return match.matches();
    }

    public static TimesForPlatform getTimes(String code) throws InvalidPlatformCodeException {
        if (!isValidCode(code)) {
            throw new InvalidPlatformCodeException();
        }

        HashMap<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("code", code);
        Document doc = Util.getXML(
            "/GetTimesForPlatform", queryParams
        );

        NodeList nodeList = doc.getElementsByTagName("Trip");
        if (nodeList == null) return null;

        ArrayList<Trip> trains = new ArrayList<Trip>();
        for (Node node : NodeListIterator.iterator(nodeList)) {
            trains.add(Trip.fromNode(node));
        }

        GetWrapper wrap = new GetWrapper((Element) doc);

        return new TimesForPlatform(
            Integer.parseInt(wrap.get("Number")),
            wrap.get("Code"),
            wrap.get("StopNumber"),
            wrap.get("Position"),
            trains
        );
    }
}
