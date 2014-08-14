package com.lysdev.transperthcached.timetable;

import java.util.Vector;


public class StopTimetable {
    String stop_num;
    Vector<Visit> weekdays;
    Vector<Visit> saturdays;
    Vector<Visit> sundays;

    private final String[] DAY_NAMES = {
        "sunday", "monday", "tuesday", "wednesday", "thursday", "friday",
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

    public Vector<Visit> getForWeekday(String day_name) {
             if (day_name.equals("monday"))    return weekdays;
        else if (day_name.equals("tuesday"))   return weekdays;
        else if (day_name.equals("wednesday")) return weekdays;
        else if (day_name.equals("thursday"))  return weekdays;
        else if (day_name.equals("friday"))    return weekdays;
        else if (day_name.equals("saturday"))  return saturdays;
        else if (day_name.equals("sunday"))    return sundays;
        else                                   return null;
    }

    public Vector<Visit> getForWeekdayNumber(int day_num) {
        assert 0 <= day_num && day_num <= 6;
        return getForWeekday(DAY_NAMES[day_num]);
    }

    public Vector<Visit> getForDayType(String day_type) {
        if (day_type.equals("weekday"))       return weekdays;
        else if (day_type.equals("saturday")) return saturdays;
        else if (day_type.equals("sunday"))   return sundays;
        else                                  return null;
    }

    // public static StopTimetable fromRaw(String stop_num, Vector<Vector<Vector<String>>> visits) {
    //     return new StopTimetable(
    //         stop_num,
    //         Visit.fromManyRaw(stop_num, visits.elementAt(0)), // weekdays
    //         Visit.fromManyRaw(stop_num, visits.elementAt(1)), // saturdays
    //         Visit.fromManyRaw(stop_num, visits.elementAt(2))  // sundays
    //     );
    // }
}
