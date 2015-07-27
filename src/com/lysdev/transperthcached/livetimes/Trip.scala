package com.lysdev.transperthcached.livetimes

import android.util.Log

import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.IllegalFieldValueException
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.DateTimeFormat

import scala.xml.Node
import scala.collection.JavaConverters._

class Trip(
        pattern: java.util.List[String],
        patternFullDisplay: java.util.List[String],
        minutesDelayTime: Int,
        cancelled: Boolean,
        actual: DateTime,
        schedule: DateTime,
        delay: Int,
        num_cars: Int,
        uid: Int,
        destination: String,
        run: String,
        state: String,
        displayDelayTime: String,
        line: String,
        lineFull: String,
        link: String,
        platform: String,
        actualDisplayTime24: LocalTime) {

    def getActual()              = this.actual
    def getActualDisplayTime24() = this.actualDisplayTime24
    def getCancelled()           = this.cancelled
    def getDelay()               = this.delay
    def getDestination()         = this.destination
    def getDisplayDelayTime()    = this.displayDelayTime
    def getLine()                = this.line
    def getLineFull()            = this.lineFull
    def getLink()                = this.link
    def getMinutesDelayTime()    = this.minutesDelayTime
    def getNumCars()             = this.num_cars
    def getPattern()             = this.pattern
    def getPatternFullDisplay()  = this.patternFullDisplay
    def getPlatform()            = this.platform
    def getRun()                 = this.run
    def getSchedule()            = this.schedule
    def getState()               = this.state
    def getUid()                 = this.uid

    override def toString() = "Run $getRun on line $getLine with $getNumCars cars"
}


object Trip {
    def parse_scheduled_correctly(s: String) : DateTime = {
        val formatter = ISODateTimeFormat.hourMinuteSecond()
        var dt_schedule : LocalTime = null
        var ref_data = DateTime.now()
        try {
            dt_schedule = formatter.parseLocalTime(s)
        } catch {
            case ifve: IllegalFieldValueException => {
                val parts = s.split(":")
                val parsed = Integer.parseInt(parts(0))
                if (parsed > 23) {
                    Log.d("TransperthCached", "Well fuck")
                    parts(0) = Integer.toString(parsed % 24)
                }
                dt_schedule = formatter.parseLocalTime(parts.mkString(":"))
                ref_data = ref_data.plusDays(1)
            }
        }

        (
            ref_data
            .withTime(
                dt_schedule.getHourOfDay(),
                dt_schedule.getMinuteOfHour(),
                dt_schedule.getSecondOfMinute(),
                0
            )
        )
    }

    class BetterNode(e: Node) {
        def apply(name: String) : String = {
            (this.e \ name).text
        }
    }

    implicit def elemBetterer(e: Node) : BetterNode = {
        new BetterNode(e)
    }

    def csv(s: String) : java.util.List[String] = {
        s.split(",").toList.asJava
    }

    def fromRaw(el: Node) : Trip = {
        val actualP = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")

        // someone inside transperth decided the best way to show that a time
        // was tomorrow, would be to simply allow the hours to go above 23.
        // we mend that here
        val dt_schedule = parse_scheduled_correctly(el("Schedule"))

        new Trip(
            csv(el("Pattern")),
            csv(el("PatternFullDisplay")),
            Integer.parseInt(el("MinutesDelayTime")),
            el("Cancelled").equals("True"),
            actualP.parseDateTime(el("Actual")),
            dt_schedule,
            Integer.parseInt(el("Delay")),
            Integer.parseInt(el("Ncar")),
            Integer.parseInt(el("Uid")),
            el("Destination"),
            el("Run"),
            el("State"),
            el("DisplayDelayTime"),
            el("Line"),
            el("LineFull"),
            el("Link"),
            el("Platform"),
            DateTimeFormat.forPattern("HH:mm").parseLocalTime(el("actualDisplayTime24"))
        )
    }
}
