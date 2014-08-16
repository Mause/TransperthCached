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


public class TimePickerFragment extends DialogFragment
                             implements TimePickerDialog.OnTimeSetListener {

    private MainActivity activity;
    private LocalTime default_time;

    public TimePickerFragment(MainActivity activity, LocalTime default_time) {
        super();
        this.default_time = default_time == null ? new LocalTime() : default_time;
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new CustomTimePickerDialog(
            getActivity(), this,

            this.default_time.getHourOfDay(),
            this.default_time.getMinuteOfHour(),

            DateFormat.is24HourFormat(getActivity())
        );
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.activity.show_for_date = this.activity.show_for_date.withTime(
            hourOfDay,
            minute,
            0, 0
        );

        this.activity.updateTimeButtonText();
    }
}
