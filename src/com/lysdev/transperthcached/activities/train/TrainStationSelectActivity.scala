package com.lysdev.transperthcached.activities.train

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.{
    Adapter,
    AdapterView,
    ListView,
    TextView
}

import org.scaloid.common._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import com.lysdev.transperthcached.{TR, R}


class TrainStationSelectActivity extends SActivity
                                 with OnItemClickListener {
    var line_name : String = null
    var direction : Direction = null

    lazy val station_list = find[ListView](TR.stations.id)

    def createOnItemClick(f: (AdapterView[_], View, Int, Long) => Unit) : OnItemClickListener = (
        new OnItemClickListener() {
            def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) : Unit = {
                f(parent, view, position, id)
            }
        }
    )

    override def onCreate(savedInstanceState: Bundle) = {
        super.onCreate(savedInstanceState)
        setContentView(TR.layout.train_stations.id)

        this.line_name = getIntent().getStringExtra("line_name")
        this.direction = Direction.from_val("direction", getIntent())

        Log.d("TransperthCached", String.format("Line: %s", line_name))

        val stations = Stations.stationNames(this.line_name).getOrElse(Seq[String]())
        this.station_list.setAdapter(SArrayAdapter(stations.toArray : _*))
        this.station_list.setOnItemClickListener(createOnItemClick(this.onItemClick))
    }

    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) = {
        val station_name = parent.getItemAtPosition(position).asInstanceOf[String]

        Log.d(
            "TransperthCached",
            String.format(
                "%s - %s - %s",
                this.direction.toString(),
                this.line_name,
                station_name
            )
        )

        startActivity(
            new Intent(this, classOf[TrainStationTimesActivity])
            .putExtra("direction", this.direction.ordinal())
            .putExtra("line_name", this.line_name)
            .putExtra("station_name", station_name)
        )
    }
}
