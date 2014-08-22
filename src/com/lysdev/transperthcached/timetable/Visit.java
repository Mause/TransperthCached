package com.lysdev.transperthcached.timetable;

import java.util.Vector;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Visit {
    public int stop_num;
    public String route_number;
    public LocalTime time;

    public Visit(int stop_num, String route_number, LocalTime time) {
        this.stop_num = stop_num;
        this.route_number = route_number;
        this.time = time;
    }

    public static Visit fromRaw(int stop_num, int route_number, int hours, int minutes) {
        LocalTime time = new LocalTime(
            hours,
            minutes,
            0
        );

        return new Visit(
            stop_num,
            String.valueOf(route_number),
            time
        );
    }

    public String toString() {
        return String.format(
            "<Visit stop_num:%s route_number:%s time:%s>",
            this.stop_num,
            this.route_number,
            this.time.toString()
        );
    }

    public String formatTime() {
        DateTimeFormatter format = DateTimeFormat.forPattern("hh:mmaa");
        return format.print(this.time);
    }
}
