package com.lysdev.transperthcached.activities.train

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, ArrayAdapter, ListAdapter, ListView, Toast}

import org.scaloid.common._
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import org.joda.time.{Minutes, DateTime}

import com.lysdev.transperthcached.livetimes.{
    Trip,
    GetTimesForStation,
    TimesForStation,
    InvalidPlatformCodeException
}
import com.lysdev.transperthcached.R


case class TripDisplayWrapper(trip: Trip) {
    override def toString() : String = {
        var s : String = this.trip.getLineFull()
        val mins : Integer = Minutes.minutesBetween(
            DateTime.now(),
            this.trip.getActual()
        ).getMinutes
        val cars : Integer = this.trip.getNumCars

        String.format(
            "%s - %d minutes - %d cars",
            s, mins, cars
        )
    }
}


class TrainStationTimesActivity extends SActivity
                                        with OnItemClickListener {
    var filtered : List[TripDisplayWrapper] = null
    lazy val times_lv = find[ListView](R.id.times)

    override def onCreate(savedInstanceState: Bundle) = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station_times)

        val line_name = getIntent().getStringExtra("line_name")
        val station_name = getIntent().getStringExtra("station_name")
        val direction = Direction.from_val("direction", getIntent())

        display_data(line_name, station_name, direction)
    }

    def display_data(line_name: String, station_name: String, direction: Direction) = {
        val tfp = GetTimesForStation.getTimes(station_name)

        val trips = tfp.getTrips().asScala.toList

        this.filtered = (
            trips
            .filter(trip => {
                val coming = trip.getDestination().equals("Perth") && direction == Direction.TO
                val going =  !trip.getDestination().equals("Perth") && direction == Direction.FROM

                going || coming
            })
            .map(new TripDisplayWrapper(_))
        )

        val ad = SArrayAdapter(this.filtered.toArray : _*)
        times_lv.setAdapter(ad)
        times_lv.setOnItemClickListener(this)
    }

    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) : Unit = {
        val trp = this.filtered(position).trip

        Log.d("TransperthCached", String.format("Clicked: %s", new TripDisplayWrapper(trp)))

        Toast.makeText(
            this,
            trp.getPatternFullDisplay().asScala.mkString(", "),
            // "Could not initialize database",
            Toast.LENGTH_LONG
        ).show()
    }
}
