package com.lysdev.transperthcached.livetimes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetTimesForStation {
    public static TimesForStation getTimes(String station_name) {
        HashMap<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("stationname", station_name);
        Document doc = Util.getXML(
            "GetSercoTimesForStation", queryParams
        );
        if (doc == null) {
            Log.d("TransperthCached", "Bad document");
            return null;
        }

        GetWrapper wrapper = new GetWrapper(doc.getDocumentElement());

        String s_last_update = wrapper.get("LastUpdate");
        DateTime dt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(
            s_last_update
        );

        NodeList nodeList = (
            doc
            .getElementsByTagName("Trips").item(0)
            .getChildNodes()
        );
        List<Trip> tripList = new ArrayList<Trip>();
        for (Node node : new NodeListIterator(nodeList)) {
            tripList.add(Trip.fromNode(node));
        }

        return new TimesForStation(
            wrapper.get("Name"),
            dt,
            tripList
        );
    }

    public static void main(String[] args) {
        getTimes("Perth Underground Stn");
    }
}
