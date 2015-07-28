package com.lysdev.transperthcached.activities.train

import java.util.ArrayList

import android.app.ProgressDialog
import android.os.Bundle
import android.os.AsyncTask
import android.util.Log
import android.os.Looper
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, ArrayAdapter, ListAdapter, TextView, ListView, Toast}

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


class FetchTimesTask[V <: android.view.View, T <: AnyRef]
                    (var klass : TrainStationTimesActivity,
                     var direction : Direction,
                     var ad : ArrayAdapter[TripDisplayWrapper])
                     extends AsyncTask[AnyRef, Void, List[Trip]] {
    var mDialog : ProgressDialog = null

    override protected def onPreExecute() {
        this.mDialog = new ProgressDialog(this.klass)
        this.mDialog.setMessage("Loading...")
        this.mDialog.setCancelable(true)
        this.mDialog.show()
    }

    override protected def doInBackground(p1: AnyRef*) : List[Trip] = {
        (
            GetTimesForStation
            .getTimes(p1.head.asInstanceOf[String])
            .getTrips().toList
        )
    }

    override protected def onPostExecute(trips: List[Trip]) {
        Log.d(
            "TransperthCached",
            String.format(
                "%s trips",
                trips.length.toString()
            )
        )

        this.klass.filtered = (
            trips
            .filter(trip => {
                val to_perth = trip.getDestination().equals("Perth")
                val coming = to_perth && this.direction == Direction.TO
                val going = !to_perth && this.direction == Direction.FROM

                going || coming
            })
            .map(new TripDisplayWrapper(_))
        )

        this.ad.clear()
        this.ad.addAll(this.klass.filtered.asJavaCollection)
        this.ad.notifyDataSetChanged()

        this.mDialog.dismiss()
        Log.d("TransperthCached", "initialized")
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
        this.filtered = List[TripDisplayWrapper]()

        display_data(line_name, station_name, direction)
    }

    def display_data(line_name: String, station_name: String, direction: Direction) = {
        // https://stackoverflow.com/questions/3200551/unable-to-modify-arrayadapter-in-listview-unsupportedoperationexception
        val ad = new ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            new ArrayList(this.filtered.asJavaCollection)
        )
        this.times_lv.setOnItemClickListener(this)
        this.times_lv.setAdapter(ad)

        (
            new FetchTimesTask[TextView, TripDisplayWrapper](this, direction, ad)
            .execute(station_name)
        )
    }

    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) : Unit = {
        val trp = this.filtered(position).trip

        Log.d("TransperthCached", String.format("Clicked: %s", new TripDisplayWrapper(trp)))

        Toast.makeText(
            this,
            trp.getPatternFullDisplay().asScala.mkString(", "),
            Toast.LENGTH_LONG
        ).show()
    }
}
