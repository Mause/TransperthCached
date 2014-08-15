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


class GetWrapper {
    private Element enode;

    public GetWrapper(Element enode) { this.enode = enode; }

    public String get(String tag) {
        return enode.getElementsByTagName(tag).item(0).getTextContent();
    }
}

public class GetTimesForPlatform {
    private static final String BASE_URL = "http://livetimes.transperth.wa.gov.au/LiveTimes.asmx/";

    public static TimesForPlatform getTimes(String code) {

        Document doc = null;
        DocumentBuilder db = null;
        URL url = null;

        try {
            url = new URL(
                BASE_URL + String.format("/GetTimesForPlatform?code=%s", code)
            );
        } catch (MalformedURLException mue) {}

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {}
        try {
            doc = db.parse(new InputSource(url.openStream()));
        } catch (SAXException se) {
        } catch (IOException ioe) {}

        doc.getDocumentElement().normalize();


        NodeList nodeList = doc.getElementsByTagName("Trip");
        if (nodeList == null) return null;

        ArrayList<TimeForPlatform> trains = new ArrayList<TimeForPlatform>();
        for (int i=0; i < nodeList.getLength(); i++) {
            GetWrapper enode = new GetWrapper(
                (Element) nodeList.item(i)
            );

            trains.add(
                new TimeForPlatform(
                    enode.get("Run"),                      // String run,
                    Integer.parseInt(enode.get("Uid")),    // int uid,
                    enode.get("Cancelled").equals("True"), // boolean cancelled,
                    Integer.parseInt(enode.get("Ncar")),   // int num_cars,
                    DateTimeFormat.forPattern("HH:mm:ss").parseLocalTime(
                        enode.get("Schedule")
                    ),   // DateTime schedule,
                    DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").parseDateTime(
                        enode.get("Actual")
                    ),      // DateTime actual,
                    Integer.parseInt(enode.get("Delay")),  // int delay,
                    enode.get("Destination"),              // String destination,
                    new ArrayList<String>(
                        Arrays.asList(enode.get("Pattern").split(","))
                    ),       // ArrayList<String> pattern,
                    enode.get("Line"),                     // String line,
                    enode.get("Link")                      // String link
                )
            );
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
