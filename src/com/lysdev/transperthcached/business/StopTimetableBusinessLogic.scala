package com.lysdev.transperthcached.business

import android.util.Log
import java.util.Collections

import com.lysdev.transperthcached.timetable.Visit
import com.lysdev.transperthcached.timetable.VisitComparator
import com.lysdev.transperthcached.timetable.Timetable
import com.lysdev.transperthcached.exceptions.StateException

import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._


object StopTimetableBusinessLogic {
    def getVisitsForStop(
            stop_num: String,
            timetable: Timetable,
            show_for_date: DateTime
        ) : List[Visit] = {

        if (stop_num.length() != 5) {
            throw new StateException(
                "Bad stop", "Please provide a 5 digit stop number"
            )
        }

        val stop_timetable = timetable.getVisitsForStop(Integer.parseInt(stop_num))
        if (stop_timetable == null) {
            val error = String.format("No such stop as %s", stop_num)
            Log.d("TransperthCached", error)

            throw new StateException("Bad stop", error)
        }

        val forDayType = stop_timetable.getForWeekdayNumber(
            show_for_date.getDayOfWeek() - 1  // getDayOfWeek returns 1 through 7
        )

        Log.d(
            "TransperthCached",
            f"Showing for day number ${show_for_date.getDayOfWeek() - 1}"
        )

        if (forDayType == null || forDayType.isEmpty()) {
            val error = String.format(
                "No stops on %s for %s",
                List[Object](
                    DateTimeFormat.forPattern("EEE, MMMM dd, yyyy").print(
                        show_for_date
                    ),
                    stop_num
                ) : _*
            )

            Log.d("TransperthCached", error)

            throw new StateException("No stops", error)
        }

        val forTime = show_for_date.toLocalTime()


        var valid = (
            forDayType
            .filter(_.getTime.isAfter(forTime))
            .sortWith(
                (a, b) => {
                    a.getTime.getMillisOfDay
                    .compareTo(b.getTime.getMillisOfDay) > 1
                }
            )
        )
        // Collections.sort(valid, new VisitComparator())

        if (valid.isEmpty()) {
            val error = "No more stops today"
            Log.d("TransperthCached", error)

            throw new StateException("No stops", error)
        }

        valid.toList
    }
}
