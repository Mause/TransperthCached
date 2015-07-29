package com.lysdev.transperthcached.activities

import java.util.ArrayList

import android.view.KeyEvent

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log

import android.view.View

import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

import com.lysdev.transperthcached.R
import com.lysdev.transperthcached.database.FavouriteStopDatabaseHelper
import com.lysdev.transperthcached.ui.FavouriteStopArrayAdapter
import com.lysdev.transperthcached.ui.FavouriteStopInputFragment

import com.lysdev.transperthcached.models.FavouriteStop

import org.scaloid.common._


class FavouriteStopsActivity extends FragmentActivity
                             with SActivity
                             with FavouriteStopInputFragment.OnFavouriteStopAddListener
                             with AdapterView.OnItemClickListener {

    var stops_adapter : FavouriteStopArrayAdapter = null
    var db : FavouriteStopDatabaseHelper = null
    lazy val stops = find[ListView](R.id.favourite_stops)

    override
    def onCreate(savedInstanceState: Bundle) = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourite_stops)

        db = new FavouriteStopDatabaseHelper(this)

        val stop_db = (
            Option(db.getFavouriteStops())
            .getOrElse(new ArrayList[FavouriteStop]())
        )

        stops_adapter = new FavouriteStopArrayAdapter(stop_db)
        stops.setAdapter(stops_adapter)
        stops.setOnItemClickListener(this)
        stops_adapter.setOnDeleteListener(this.onDelete)
    }

    def onDelete(fav: FavouriteStop) {
        Log.d("TransperthCached", "Delete: " + fav.toString())
        db.deleteStop(fav)
        updateStops()
    }

    override
    def onDestroy() {
        super.onDestroy()
        db.close()
    }

    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        val selected_stop = (
            parent
            .getItemAtPosition(position)
            .asInstanceOf[FavouriteStop]
        )

        Log.d("TransperthCached", "Selected stop: " + selected_stop.toString())

        // http://stackoverflow.com/questions/19286970/using-intents-to-pass-data-between-activities-in-android
        startActivity(
            new Intent(this, classOf[MainActivity])
            .putExtra("stop_num", selected_stop.getStopNumber)
        )
    }

    def deleteButtonClicked(view: View) {}
    def    addButtonClicked(view: View) { addButtonClicked() }
    def    addButtonClicked() {
        new FavouriteStopInputFragment(this)
        .show(getSupportFragmentManager(), "favouriteStopInput")
    }

    def onFavouriteStopAdd(stop_number: String, dialog: DialogInterface) {
        if (stop_number.length() == 5) {
            Log.d("TransperthCached", "Stop number: " + stop_number)

            val istop_number = Integer.parseInt(stop_number)
            if (db.stopExists(istop_number)) {
                Toast.makeText(
                    this,
                    R.string.favourite_stop_exists,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                db.addFavouriteStop(new FavouriteStop(istop_number))

                updateStops()
            }

            dialog.dismiss()
        } else {
            Log.d("TransperthCached", "Not long enough")

            Toast.makeText(
                this,
                R.string.stop_number_not_long_enough,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override
    def onKeyUp(keyCode: Int, event: KeyEvent) : Boolean = {
        if (keyCode != KeyEvent.KEYCODE_MENU) {
            super.onKeyUp(keyCode, event)
        }

        addButtonClicked()
        true
    }

    def updateStops() {
        stops_adapter.clear()
        stops_adapter.addAll(db.getFavouriteStops())
        stops_adapter.notifyDataSetChanged()
    }

    // protected void onSaveInstanceState(Bundle outState) {
    //     outState.putString("stop_number", stop_num_widget.getText().toString())
    // }
}
