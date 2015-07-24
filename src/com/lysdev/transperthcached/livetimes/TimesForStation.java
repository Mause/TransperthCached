package com.lysdev.transperthcached.livetimes;

import java.util.List;
import org.joda.time.DateTime;


public class TimesForStation {
    private String station_name;
    private DateTime last_updated;
    private List<Trip> trips;

    public TimesForStation(String station_name, DateTime last_updated, List<Trip> trips) {
        this.station_name = station_name;
        this.last_updated = last_updated;
        this.trips = trips;
    }

    public List<Trip> getTrips() {
        return this.trips;
    }

    public String toString() {
        return String.format(
            "<TimesForStation %s - %s - %d trips>",
            this.station_name,
            this.last_updated.toString(),
            this.trips.size()
        );
    }
}
