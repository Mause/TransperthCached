package com.lysdev.transperthcached.activities.train

import org.scaloid.common._
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.{
    AdapterView,
    ArrayAdapter,
    ListAdapter,
    ListView,
    TextView
}

import com.lysdev.transperthcached.R

class TrainLineSelectActivity extends SActivity
                              with OnItemClickListener {
    var direction : Direction = null

    lazy val line_list = find[ListView](R.id.stations)

    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.train_stations)

        this.direction = Direction.from_val("direction", getIntent())

        val lines = Stations.lineNames()
        this.line_list.setAdapter(SArrayAdapter(lines:_*))
        this.line_list.setOnItemClickListener(this)
    }

    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) : Unit = {
        startActivity(
            new Intent(this, classOf[TrainStationSelectActivity])
            .putExtra("direction", this.direction.ordinal())
            .putExtra("line_name", (view.asInstanceOf[TextView].getText().toString()))
        )
    }
}
