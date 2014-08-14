package com.lysdev.transperthcached.silverrails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.lysdev.transperthcached.MyLocation;
import com.lysdev.transperthcached.MyLocation.LocationResult;
import com.lysdev.transperthcached.Util;


public class NearbyTransitStop extends TransitStop {
    public NearbyTransitStop(
        int distance,

        double latitude, double longitude,
        String description,
        int code
        ) {
        super(
            latitude, longitude, description, code
        );
        this.distance = distance;
    }

    public String toString() {
        return String.format(
            "%d metres to %s",
            getDistance().intValue(),
            getDescription()
        );
    }


    Integer distance;

    public Integer getDistance() {
        return this.distance;
    }
}
