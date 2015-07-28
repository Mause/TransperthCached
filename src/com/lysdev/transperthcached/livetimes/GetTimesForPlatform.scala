package com.lysdev.transperthcached.livetimes

import java.util.regex.Pattern


object GetTimesForPlatform {
    lazy val patt = Pattern.compile("[A-Z]{3}\\d")

    def isValidCode(code: String) = patt.matcher(code).matches()

    def getTimes(code: String) : TimesForPlatform = {
        if (!isValidCode(code)) {
            throw new InvalidPlatformCodeException()
        }

        val doc = Util.getXML(
            "/GetTimesForPlatform", Seq("code" -> code).toMap
        )

        val trains = (
            (doc \ "Trip")
            .map(Trip.fromRaw(_))
            .toList
        )

        new TimesForPlatform(
            Integer.parseInt((doc \ "Number").text.trim()),
            (doc \ "Code").text,
            (doc \ "StopNumber").text,
            (doc \ "Position").text,
            trains
        )
    }
}
