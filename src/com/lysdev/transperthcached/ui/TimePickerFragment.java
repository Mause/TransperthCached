package com.lysdev.transperthcached.ui;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import org.joda.time.LocalTime;

import com.lysdev.transperthcached.MainActivity;


public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;
    private LocalTime default_time;

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener, LocalTime default_time) {
        super();
        this.default_time = default_time == null ? new LocalTime() : default_time;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // return new TimePickerDialog(
        return new CustomTimePickerDialog(
            getActivity(), listener,

            this.default_time.getHourOfDay(),
            this.default_time.getMinuteOfHour(),

            DateFormat.is24HourFormat(getActivity())
        );
    }
}
