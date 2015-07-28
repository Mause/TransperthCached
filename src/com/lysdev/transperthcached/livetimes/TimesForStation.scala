package com.lysdev.transperthcached.livetimes;

import org.joda.time.DateTime;


class TimesForStation(
        station_name: String,
        last_updated: DateTime,
        trips: List[Trip]) {

    def getTrips()       = this.trips
    def getLastUpdated() = this.last_updated
    def getStationName() = this.station_name

    override def toString() = "<TimesForStation $getStationName - $getLastUpdated - ${getTrips.size} trips>"
}
