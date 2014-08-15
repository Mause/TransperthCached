package com.lysdev.transperthcached.livetimes;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class TimesForPlatform {
    private int number;
    private String code;
    private String stop_number;
    private String position;

    private ArrayList<TimeForPlatform> times;

    public TimesForPlatform(
        int number,
        String code,
        String stop_number,
        String position,
        ArrayList<TimeForPlatform> times) {

        this.number = number;
        this.code = code;
        this.stop_number = stop_number;
        this.position = position;
        this.times = times;
    }

    public int getNumber()      { return this.number; }
    public String getCode()     { return this.code; }
    public String getStopNumber()  { return this.stop_number; }
    public String getPosition() { return this.position; }
    public ArrayList<TimeForPlatform> getTimes() { return this.times; }

    public String toString() {
        return String.format(
            "Platform %s at %s with stop number %d",
            getCode(),
            getPosition(),
            getStopNumber()
        );
    }
}
