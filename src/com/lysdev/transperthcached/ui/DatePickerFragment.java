package com.lysdev.transperthcached.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.joda.time.DateTime;



public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;
    private DateTime default_date;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener, DateTime default_date) {
        super();
        this.listener = listener;
        this.default_date = default_date == null ? new DateTime() : default_date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(
            getActivity(),
            listener,
            this.default_date.getYear(),
            this.default_date.getMonthOfYear() - 1, // android uses zero indexed months
            this.default_date.getDayOfMonth()
        );
    }
}
