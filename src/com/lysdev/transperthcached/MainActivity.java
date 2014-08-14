package com.lysdev.transperthcached;

// Standard library
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

// android sdk
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

// Joda
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalTime;

// Project specific
import com.lysdev.transperthcached.timetable.StopTimetable;
import com.lysdev.transperthcached.timetable.Timetable;
import com.lysdev.transperthcached.timetable.Visit;
import com.lysdev.transperthcached.timetable.VisitComparator;

import com.lysdev.transperthcached.ui.DatePickerFragment;
import com.lysdev.transperthcached.ui.TimePickerFragment;


public class MainActivity extends FragmentActivity {
    private Timetable timetable;
    ListView stop_display;
    ArrayAdapter<String> stop_display_source;

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
        } catch (java.io.IOException ioe) {
            AlertDialog dialog = new AlertDialog.Builder(
                getApplicationContext()
            ).create();
            dialog.show();
        }

        Log.d("TransperthCached", "initialized");

        stop_display = (ListView) findViewById(R.id.visits);

        ArrayList<String> visits = new ArrayList<String>();
        stop_display_source = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            visits
        );
        stop_display.setAdapter(stop_display_source);

        show_for_date = new DateTime();
        updateTimeButtonText();
        updateDateButtonText();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.timetable.onDestroy();
    }

    public void showForStop(View view) {
        EditText stop_num_widget = (EditText) findViewById(R.id.stop_number);

        String stop_num = stop_num_widget.getText().toString();
        if (stop_num == null) return;

        StopTimetable stop_timetable = this.timetable.getVisitsForStop(stop_num);
        if (stop_timetable == null) {
            Log.d("TransperthCached", String.format("No such stop as %s", stop_num));

            AlertDialog dialog = new AlertDialog.Builder(
                getApplicationContext()
            ).create();
            dialog.show();

            return;
        }

        Vector<Visit> forDayType = stop_timetable.getForWeekdayNumber(
            show_for_date.getDayOfWeek()
        );

        if (forDayType == null || forDayType.isEmpty()) {
            Log.d(
                "TransperthCached",
                String.format(
                    "No stops on (%s) for %s",
                    show_for_date, stop_num
                )
            );
            return;
        }

        LocalTime forTime = this.show_for_date.toLocalTime();

        Vector<Visit> valid = new Vector<Visit>();
        for (Visit visit : forDayType) {
            if (visit.time.isAfter(forTime)) {
                valid.add(visit);
            }
        }
        Collections.sort(valid, new VisitComparator());

        if (valid.isEmpty()) {
            Log.d("TransperthCached", "No more stops today");
            return;
        } else {
            Log.d("TransperthCached", valid.toString());
        }

        displayVisits(valid);
    }

    public void timeSelectButtonClicked(View v) {
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

        Log.d("TransperthCached", "Clearing display");


        stop_display_source.clear();

        Log.d("TransperthCached", "Displaying");
        for (Visit visit : visits) {

            Log.d("TransperthCached", "Displaying: " + visit.toString());

            stop_display_source.add(
                String.format(
                    "%s : %s",
                    visit.route_number,
                    visit.formatTime()
                    // visit.time.toString()
                )
            );
        }
    }
}
