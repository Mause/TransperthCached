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


class Visit(val stop_num: Int, val route_number: String, val time: LocalTime) {
    override def toString() = f"<Visit stop_num:$stop_num route_number:$route_number time:$time>"

    def getTime = this.time
    def getRouteNumber = this.route_number

    def formatTime = DateTimeFormat.forPattern("hh:mmaa").print(this.time)
}
