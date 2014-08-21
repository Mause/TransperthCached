package com.lysdev.transperthcached.models;

public class FavouriteStop {
    private String stop_number;
    private Integer sid = null;

    public FavouriteStop(String stop_number) {
        this.stop_number = stop_number;
    }

    public FavouriteStop(Integer sid, String stop_number) {
        this(stop_number);
        this.sid = sid;
    }

    public String getStopNumber() {
        return this.stop_number;
    }

    public String toString() {
        return this.stop_number;
    }
}
