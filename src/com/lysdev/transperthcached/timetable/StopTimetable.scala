package com.lysdev.transperthcached.timetable

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._


class StopTimetable(stop_num: Int,
                    weekdays: List[Visit],
                    saturdays: List[Visit],
                    sundays: List[Visit]) {

    def this(stop_num: Int, weekdays: java.util.Vector[Visit], saturdays: java.util.Vector[Visit], sundays: java.util.Vector[Visit]) {
        this(
            stop_num,
            weekdays.asScala.toList,
            saturdays.asScala.toList,
            sundays.asScala.toList
        )
    }

    val DAY_NAMES = List(
        "sunday",
        "monday",
        "tuesday",
        "wednesday",
        "thursday",
        "friday",
        "saturday"
    )

    override def toString() = "<StopTimetable for $stop_num>"

    def getForWeekdayNumber(day_num: Int) : List[Visit] = {
        assert(0 <= day_num && day_num <= 6)

        if (0 <= day_num && day_num <= 4) {
            return weekdays
        } else if (day_num == 5) {
            return saturdays
        } else if (day_num == 6) {
            return sundays
        } else {
            throw new java.lang.Error("Bad day_num")
        }
    }

    def getForDayType(day_type: String) : List[Visit] = {
        day_type match {
            case "weekday"  => weekdays
            case "saturday" => saturdays
            case "sunday"   => sundays
            case _          => null
        }
    }
}
