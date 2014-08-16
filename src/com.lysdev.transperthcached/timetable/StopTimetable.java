package com.lysdev.transperthcached.timetable;

import java.util.Vector;


public class StopTimetable {
    String stop_num;
    Vector<Visit> weekdays;
    Vector<Visit> saturdays;
    Vector<Visit> sundays;

    private final String[] DAY_NAMES = {
        "sunday",
        "monday",
        "tuesday",
        "wednesday",
        "thursday",
        "friday",
        "saturday"
    };

    public StopTimetable(
            String stop_num,
            Vector<Visit> weekdays,
            Vector<Visit> saturdays,
            Vector<Visit> sundays) {
        this.stop_num = stop_num;
        this.weekdays = weekdays;
        this.saturdays = saturdays;
        this.sundays = sundays;
    }

    public String toString() {
        return String.format(
            "<StopTimetable for %s>",
            stop_num
        );
    }

    public Vector<Visit> getForWeekdayNumber(int day_num) {
        assert 0 <= day_num && day_num <= 6;

        if (0 <= day_num && day_num <= 4) {
            return weekdays;
        } else if (day_num == 5) {
            return saturdays;
        } else if (day_num == 6) {
            return sundays;
        } else {
            throw new java.lang.Error("Bad day_num");
        }
    }

    public Vector<Visit> getForDayType(String day_type) {
        if (day_type.equals("weekday"))       return weekdays;
        else if (day_type.equals("saturday")) return saturdays;
        else if (day_type.equals("sunday"))   return sundays;
        else                                  return null;
    }
}
