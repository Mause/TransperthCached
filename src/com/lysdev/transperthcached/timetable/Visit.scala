package com.lysdev.transperthcached.timetable

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


object Visit {
    def fromRaw(stop_num: Int, route_number: Int, hours: Int, minutes: Int) : Visit = {
        val time = new LocalTime(
            hours,
            minutes,
            0
        )

        new Visit(
            stop_num,
            String.valueOf(route_number),
            time
        )
    }
}


class Visit(stop_num: Int, route_number: String, time: LocalTime) {

    // override def toString : String = String.format(
    //     "<Visit stop_num:%d route_number:%s time:%s>",
    //     this.stop_num,
    //     this.route_number,
    //     this.time.toString()
    // )

    def formatTime = DateTimeFormat.forPattern("hh:mmaa").print(this.time)
}
