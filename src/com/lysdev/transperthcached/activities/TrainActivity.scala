package com.lysdev.transperthcached.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.lysdev.transperthcached.activities.train.TrainLineSelectActivity
import com.lysdev.transperthcached.activities.train.Direction
import com.lysdev.transperthcached.R

class TrainActivity extends Activity {
    override
    def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trains)
    }

    def onClickDirection(viewery: View) {
        val button = viewery.asInstanceOf[Button]
        val dir = (
            if (button.getId() == R.id.from_perth) Direction.FROM
            else                                   Direction.TO
        )

        startActivity(
            new Intent(this, classOf[TrainLineSelectActivity])
            .putExtra("direction", dir.ordinal())
        )
    }
}
