package com.lysdev.transperthcached.ui;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;

import com.lysdev.transperthcached.MainActivity;
import com.lysdev.transperthcached.R;


public class DatePickerFragment extends DialogFragment
                             implements DatePickerDialog.OnDateSetListener {

    private MainActivity activity;
    private DateTime default_date;

    public DatePickerFragment(MainActivity activity, DateTime default_date) {
        super();
        this.activity = activity;
        this.default_date = default_date == null ? new DateTime() : default_date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(
            DatePickerFragment.this.getActivity(),
            this,
            this.default_date.getYear(),
            this.default_date.getMonthOfYear(),
            this.default_date.getDayOfMonth()
        );
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.activity.show_for_date = this.activity.show_for_date.withDate(
            year,
            month,
            day
        );

        this.activity.updateDateButtonText();
    }
}
