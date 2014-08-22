package com.lysdev.transperthcached.activities;

// Standard library
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

// android sdk
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import android.location.Location;

// Joda
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

// Project specific
import com.lysdev.transperthcached.timetable.Timetable;
import com.lysdev.transperthcached.timetable.Visit;

import com.lysdev.transperthcached.ui.DatePickerFragment;
import com.lysdev.transperthcached.ui.OkDialog;
import com.lysdev.transperthcached.ui.SelectStopDialog;
import com.lysdev.transperthcached.ui.SelectStopDialogOnSelected;
import com.lysdev.transperthcached.ui.TimePickerFragment;

import com.lysdev.transperthcached.R;

import com.lysdev.transperthcached.exceptions.StateException;
import com.lysdev.transperthcached.silverrails.GetNearbyTransitStops;
import com.lysdev.transperthcached.silverrails.NearbyTransitStop;
import com.lysdev.transperthcached.utils.MyLocation.LocationResult;
import com.lysdev.transperthcached.utils.MyLocation;
import com.lysdev.transperthcached.utils.Util;


public class StopTimetableActivity extends FragmentActivity {
    private Timetable timetable;

    ListView stop_display;
    EditText stop_num_widget;
    ArrayAdapter<String> stop_display_source;

    // TODO: mend the issue mentioned on the line below
    public DateTime show_for_date; // public so the ui fragment can pass back data

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("TransperthCached", "initializing StopTimetableActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_timetable);

        this.timetable = new Timetable();

        if (savedInstanceState == null) {
            Log.d("TransperthCached", "No bundled state received");
        } else {
            Log.d("TransperthCached", "Bundled state received, rebuilding app");
        }

        preUiLoadSaveInstanceState(savedInstanceState);
        setupUI();
        postUiLoadSaveInstanceState(savedInstanceState);
        Log.d("TransperthCached", "initialized StopTimetableActivity");
    }

    protected void setupUI() {
        stop_display =    (ListView) findViewById(R.id.visits);
        stop_num_widget = (EditText) findViewById(R.id.stop_number);

        stop_display_source = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            new ArrayList<String>()
        );
        stop_display.setAdapter(stop_display_source);

        updateTimeButtonText();
        updateDateButtonText();
    }

    protected void preUiLoadSaveInstanceState(Bundle inState) {
        if (inState == null) {
            show_for_date = new DateTime();
        } else {
            show_for_date = DateTime.parse(
                inState.getString("show_for_date")
            );
        }
    }

    protected void postUiLoadSaveInstanceState(Bundle inState) {
        if (inState != null) {
            // cannot set this till the ui is setup
            stop_num_widget.setText(
                inState.getString("stop_number")
            );
        }

        // this is for when a user selects a stop in favourites
        Intent intent = getIntent();
        if (intent != null) {
            String stop_num = intent.getStringExtra("stop_num");

            if (stop_num != null) {
                stop_num_widget.setText(stop_num);
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("show_for_date", show_for_date.toString());

        outState.putString("stop_number", stop_num_widget.getText().toString());
    }

    private void ok_dialog(String title, String message) {
        OkDialog dialog = new OkDialog();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        dialog.setArguments(args);

        // dialog.setTargetFragment(this, 0);
        dialog.show(getSupportFragmentManager(), "TransperthCached");
    }

    public void showForStop(View view) {
        Util.hideSoftKeyboard(this);

        String stop_num = stop_num_widget.getText().toString();
        if (stop_num == null) return;

        Vector<Visit> visits = null;
        try {
            visits = StopTimetableBusinessLogic.getVisitsForStop(
                stop_num, timetable, show_for_date
            );

        } catch (StateException state) {
            stop_display_source.clear();
            this.ok_dialog(state.getTitle(), state.getMessage());
        }

        if (visits == null) return;
        displayVisits(visits);
    }

    public void timeSelectButtonClicked(View v) {
        Util.hideSoftKeyboard(this);

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                show_for_date = show_for_date.withTime(
                    hourOfDay,
                    minute,
                    0, 0
                );

                updateTimeButtonText();
            }
        };

        new TimePickerFragment(
            listener, this.show_for_date.toLocalTime()
        ).show(getSupportFragmentManager(), "timePicker");
    }

    public void updateTimeButtonText() {
        Button button = (Button) findViewById(R.id.time_select_button);
        button.setText(
            DateTimeFormat.forPattern("hh:mmaa").print(
                show_for_date.toLocalTime()
            )
        );
    }

    public void dateSelectButtonClicked(View v) {
        Util.hideSoftKeyboard(this);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                show_for_date = show_for_date.withDate(
                    year,
                    month + 1,
                    day
                );

                updateDateButtonText();
            }
        };

        new DatePickerFragment(
            listener, this.show_for_date
        ).show(getSupportFragmentManager(), "datePicker");
    }

    public void updateDateButtonText() {
        Button button = (Button) findViewById(R.id.date_select_button);
        button.setText(
            DateTimeFormat.forPattern("EEE, MMMM dd, yyyy").print(
                show_for_date
            )
        );
    }

    private void displayVisits(Vector<Visit> visits) {
        // clear so we can display the new data
        stop_display_source.clear();

        for (Visit visit : visits) {
            stop_display_source.add(
                String.format(
                    "%s : %s",
                    visit.route_number,
                    visit.formatTime()
                )
            );
        }

        stop_display.setSelection(0);
    }

    public void nearbyButtonClicked(View v) {
        final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Determining location...");
                mDialog.setCancelable(false);
                mDialog.show();

        LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location){
                mDialog.dismiss();
                nearbyButtonCallback(location);
            }
        };

        new MyLocation().getLocation(this, locationResult);
    }

    // should stop multiple dialogs
    private boolean waiting_for_user_selection = false;
    public void nearbyButtonCallback(Location location) {
        if (waiting_for_user_selection) {
            Log.d("TransperthCached", "Dialog already open");
            return;
        }
        waiting_for_user_selection = true;

        ArrayList<NearbyTransitStop> stops = GetNearbyTransitStops.getNearby(
            getResources().getString(R.string.silverrails_apikey),
            location.getLatitude(),
            location.getLongitude()
        );

        SelectStopDialogOnSelected callback = new SelectStopDialogOnSelected() {
            public void onSelected(NearbyTransitStop stop) {
                waiting_for_user_selection = false;
                stop_num_widget.setText(stop.getCode());
            }
        };

        new SelectStopDialog(stops, callback).show(
            getSupportFragmentManager(),
            "TransperthCached"
        );
    }
}
