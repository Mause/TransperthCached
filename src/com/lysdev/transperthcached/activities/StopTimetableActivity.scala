package com.lysdev.transperthcached.activities

// Standard library
import java.util.ArrayList

// android sdk
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log

import android.view.View

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TimePicker

import android.location.Location

// Joda
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

// Project specific
import com.lysdev.transperthcached.timetable.Timetable
import com.lysdev.transperthcached.timetable.Visit

import com.lysdev.transperthcached.ui.DatePickerFragment
import com.lysdev.transperthcached.ui.OkDialog
import com.lysdev.transperthcached.ui.SelectStopDialog
import com.lysdev.transperthcached.ui.TimePickerFragment

import com.lysdev.transperthcached.R

import com.lysdev.transperthcached.exceptions.StateException

import com.lysdev.transperthcached.silverrails.GetNearbyTransitStops
import com.lysdev.transperthcached.silverrails.NearbyTransitStop

import com.lysdev.transperthcached.utils.MyLocation
import com.lysdev.transperthcached.utils.Util

import com.lysdev.transperthcached.business.StopTimetableBusinessLogic

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import org.scaloid.common._

class StopTimetableActivity extends FragmentActivity with SActivity {
    var timetable : Timetable = null
    var stop_display_source : ArrayAdapter[String] = null
    var show_for_date : DateTime = null

    lazy val stop_display = find[ListView](R.id.visits)
    lazy val stop_num_widget = find[EditText](R.id.stop_number)
    lazy val date_button = find[Button](R.id.date_select_button)
    lazy val time_button = find[Button](R.id.time_select_button)

    /** Called when the activity is first created. */
    override
    def onCreate(savedInstanceState: Bundle) {
        Log.d("TransperthCached", "initializing StopTimetableActivity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stop_timetable)

        this.timetable = new Timetable()

        if (savedInstanceState == null) {
            Log.d("TransperthCached", "No bundled state received")
        } else {
            Log.d("TransperthCached", "Bundled state received, rebuilding app")
        }

        preUiLoadSaveInstanceState(savedInstanceState)
        setupUI()
        postUiLoadSaveInstanceState(savedInstanceState)
        Log.d("TransperthCached", "initialized StopTimetableActivity")
    }

    def setupUI() {
        stop_display_source = new ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            new ArrayList[String]()
        )
        stop_display.setAdapter(stop_display_source)

        updateTimeButtonText()
        updateDateButtonText()
    }

    def preUiLoadSaveInstanceState(inState: Bundle) {
        if (inState == null) {
            show_for_date = new DateTime()
        } else {
            show_for_date = DateTime.parse(
                inState.getString("show_for_date")
            )
        }
    }

    def postUiLoadSaveInstanceState(inState: Bundle) {
        if (inState != null) {
            // cannot set this till the ui is setup
            stop_num_widget.setText(
                inState.getString("stop_number")
            )
        }

        // this is for when a user selects a stop in favourites
        val intent = getIntent()
        if (intent != null) {
            val stop_num = intent.getIntExtra("stop_num", 0)

            if (stop_num != 0) {
                stop_num_widget.setText(
                    String.valueOf(stop_num)
                )
            }
        }
    }

    override def onSaveInstanceState(outState: Bundle) {
        outState.putString("show_for_date", show_for_date.toString())

        // this seems to work as is no need to stringy or parseInt
        outState.putString("stop_number", stop_num_widget.getText().toString())
    }

    def showForStop(view: View) {
        Util.hideSoftKeyboard(this)

        val stop_num = stop_num_widget.getText().toString()
        if (stop_num == null) return

        var visits : List[Visit] = null
        try {
            visits = StopTimetableBusinessLogic.getVisitsForStop(
                stop_num, timetable, show_for_date
            ).asScala.toList

        } catch {
            case state: StateException => {
                stop_display_source.clear()
                OkDialog.ok_dialog(state.getTitle(), state.getMessage())
            }
        }

        if (visits == null) return
        displayVisits(visits)
    }

    def timeSelectButtonClicked(v: View) {
        Util.hideSoftKeyboard(this)

        val listener = new TimePickerDialog.OnTimeSetListener() {
            def onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
                show_for_date = show_for_date.withTime(
                    hourOfDay,
                    minute,
                    0, 0
                )

                updateTimeButtonText()
            }
        }

        new TimePickerFragment(
            listener, this.show_for_date.toLocalTime()
        ).show(getSupportFragmentManager(), "timePicker")
    }

    def updateTimeButtonText() {
        time_button.setText(
            DateTimeFormat.forPattern("hh:mmaa").print(
                show_for_date.toLocalTime()
            )
        )
    }

    def dateSelectButtonClicked(v: View) {
        Util.hideSoftKeyboard(this)

        val listener = new DatePickerDialog.OnDateSetListener() {
            def onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                show_for_date = show_for_date.withDate(
                    year,
                    month + 1,
                    day
                )

                updateDateButtonText()
            }
        }

        new DatePickerFragment(
            listener, this.show_for_date
        ).show(getSupportFragmentManager(), "datePicker")
    }

    def updateDateButtonText() {
        date_button.setText(
            DateTimeFormat.forPattern("EEE, MMMM dd, yyyy").print(
                show_for_date
            )
        )
    }

    def displayVisits(visits: List[Visit]) {
        // clear so we can display the new data
        stop_display_source.clear()

        stop_display_source.addAll(
            visits.map(visit =>
                String.format(
                    "%s : %s",
                    visit.getRouteNumber,
                    visit.formatTime
                )
            )
        )

        stop_display.setSelection(0)
    }

    def nearbyButtonClicked(v: View) {
        val mDialog = new ProgressDialog(this)
        mDialog.setMessage("Determining location...")
        mDialog.setCancelable(false)
        mDialog.show()

        new MyLocation().getLocation((location: Location) => {
            mDialog.dismiss()
            nearbyButtonCallback(location)
        })
    }

    // should stop multiple dialogs
    var waiting_for_user_selection : Boolean = false
    def nearbyButtonCallback(location: Location) {
        if (waiting_for_user_selection) {
            Log.d("TransperthCached", "Dialog already open")
            return
        }
        waiting_for_user_selection = true

        val api_key = getResources().getString(R.string.silverrails_apikey)

        val stops = GetNearbyTransitStops.getNearby(
            api_key,
            location.getLatitude(),
            location.getLongitude()
        )

        val callback = (stop: NearbyTransitStop) => {
            waiting_for_user_selection = false
            stop_num_widget.setText(stop.getCode())
        }

        new SelectStopDialog(stops, callback).show(
            getSupportFragmentManager(),
            "TransperthCached"
        )
    }
}
