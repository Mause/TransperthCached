package com.lysdev.transperthcached;

import java.util.Vector;
import android.util.Log;
import java.util.Collections;


import com.lysdev.transperthcached.timetable.Visit;
import com.lysdev.transperthcached.timetable.VisitComparator;
import com.lysdev.transperthcached.timetable.StopTimetable;
import com.lysdev.transperthcached.timetable.Timetable;
import com.lysdev.transperthcached.StateException;


import org.joda.time.LocalTime;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class StopTimetableBusinessLogic {
    public static Vector<Visit> getVisitsForStop(
            String stop_num,
            Timetable timetable,
            DateTime show_for_date
        ) throws StateException {


        if (stop_num.length() != 5) {
            throw new StateException(
                "Bad stop", "Please provide a 5 digit stop number"
            );
        }

        StopTimetable stop_timetable = timetable.getVisitsForStop(stop_num);
        if (stop_timetable == null) {
            String error = String.format("No such stop as %s", stop_num);
            Log.d("TransperthCached", error);

            throw new StateException("Bad stop", error);
        }

        Vector<Visit> forDayType = stop_timetable.getForWeekdayNumber(
            show_for_date.getDayOfWeek() - 1  // getDayOfWeek returns 1 through 7
        );

        Log.d(
            "TransperthCached",
            String.format(
                "Showing for day number %d",
                show_for_date.getDayOfWeek() - 1
            )
        );

        if (forDayType == null || forDayType.isEmpty()) {
            String error = String.format(
                "No stops on %s for %s",
                DateTimeFormat.forPattern("EEE, MMMM dd, yyyy").print(
                    show_for_date
                ),
                stop_num
            );

            Log.d("TransperthCached", error);

            throw new StateException("No stops", error);
        }

        LocalTime forTime = show_for_date.toLocalTime();

        Vector<Visit> valid = new Vector<Visit>();
        for (Visit visit : forDayType) {
            if (visit.time.isAfter(forTime)) {
                valid.add(visit);
            }
        }
        Collections.sort(valid, new VisitComparator());

        if (valid.isEmpty()) {
            String error = "No more stops today";
            Log.d("TransperthCached", error);

            throw new StateException(
                "No stops", error
            );
        }

        return valid;
    }
}
