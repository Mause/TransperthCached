package com.lysdev.transperthcached.livetimes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        if (doc == null) return null;

        NodeList nodeList = doc.getElementsByTagName("Trip");
        if (nodeList == null) return null;

        ArrayList<Trip> trains = new ArrayList<Trip>();
        for (Node node : NodeListIterator.iterator(nodeList)) {
            trains.add(Trip.fromNode(node));
        }

        GetWrapper wrap = new GetWrapper(doc.getDocumentElement());
        return new TimesForPlatform(
            Integer.parseInt(wrap.get("Number").trim()),
            wrap.get("Code"),
            wrap.get("StopNumber"),
            wrap.get("Position"),
            trains
        );
    }
}
