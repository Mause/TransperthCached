package com.lysdev.transperthcached;

// Standard library
import java.io.IOException;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

// android sdk
import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.view.inputmethod.InputMethodManager;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import com.lysdev.transperthcached.MyLocation.LocationResult;
import com.lysdev.transperthcached.silverrails.GetNearbyTransitStops;
import com.lysdev.transperthcached.silverrails.NearbyTransitStop;


public class MainActivity extends FragmentActivity {
    private Timetable timetable;

    ListView stop_display;
    EditText stop_num_widget;
    ArrayAdapter<String> stop_display_source;

    // TODO: mend the issue mentioned on the line below
    public DateTime show_for_date; // public so the ui fragment can pass back data

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.timetable = new Timetable();
        Log.d("TransperthCached", "initializing");

        try {
            this.timetable.initialize(getApplicationContext());
        } catch (java.lang.Error e) {
            ok_dialog("Could not initialize database", null);
            throw new java.lang.Error(e.toString());
        } catch (java.io.IOException e) {
            ok_dialog("Could not initialize database", null);
            throw new java.lang.Error(e.toString());
        }

        Log.d("TransperthCached", "initialized");

        if (savedInstanceState == null) {
            Log.d("TransperthCached", "No bundled state received");
        } else {
            Log.d("TransperthCached", "Bundled state received, rebuilding app");
        }

        preUiLoadSaveInstanceState(savedInstanceState);
        setupUI();
        postUiLoadSaveInstanceState(savedInstanceState);
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
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("show_for_date", show_for_date.toString());

        outState.putString("stop_number", stop_num_widget.getText().toString());
    }

    protected void onDestroy() {
        super.onDestroy();
        this.timetable.onDestroy();
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
        hideSoftKeyboard();

        String stop_num = stop_num_widget.getText().toString();
        if (stop_num == null) return;

        Vector<Visit> visits = null;
        try {
            visits = MainActivityBusinessLogic.getVisitsForStop(
                stop_num, timetable, show_for_date
            );

        } catch (StateException state) {
            stop_display_source.clear();
            this.ok_dialog(state.getTitle(), state.getMessage());
        }

        if (visits == null) return;
        displayVisits(visits);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager;

        inputMethodManager = (InputMethodManager) getSystemService(
            Activity.INPUT_METHOD_SERVICE
        );
        inputMethodManager.hideSoftInputFromWindow(
            getCurrentFocus().getWindowToken(),
            0
        );
    }

    public void timeSelectButtonClicked(View v) {
        hideSoftKeyboard();

        DialogFragment newFragment = new TimePickerFragment(
            this, this.show_for_date.toLocalTime()
        );
        newFragment.show(getSupportFragmentManager(), "timePicker");
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
        hideSoftKeyboard();

        DialogFragment newFragment = new DatePickerFragment(
            this, this.show_for_date
        );
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
                MainActivity.this.nearbyButtonCallback(location);
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
