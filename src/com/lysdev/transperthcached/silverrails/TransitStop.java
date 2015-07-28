package com.lysdev.transperthcached.silverrails;

import java.util.Vector;

import android.location.Location;

public class TransitStop {
    private int code;
    private int zone;
    private Location position;
    private String dataSet;
    private String description;
    private String stopUid;
    private String[] routes;
    private String[] supportedModes;

    private double latitude, longitude;

    public TransitStop(double latitude, double longitude, String description, int code) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.position = new Location("TransitStop");
        this.position.setLatitude(latitude);
        this.position.setLongitude(longitude);

        this.description = description;
        this.code = code;
    }

    public Location getPosition() {
        return this.position;
    }

    public String getCode() {
        return Integer.toString(code);
    }

    public String getDescription() {
        return this.description;
    }
}

    // DataSet = "PerthRestricted"
    // Code = "11990"
    // StopUid = "PerthRestricted:11990"
    // Description = "Walanna Dr Before Lowan Loop"
    // Position = "-32.0055906, 115.8858706"
    // Zone = "1"
    // SupportedModes = "Bus"
    // Routes = "PerthRestricted:39002;PerthRestricted:39146"
