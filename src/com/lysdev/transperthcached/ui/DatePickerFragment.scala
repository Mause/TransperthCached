package com.lysdev.transperthcached.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

import org.joda.time.DateTime


class DatePickerFragment(listener: DatePickerDialog.OnDateSetListener,
                         default_date: DateTime)
                        extends DialogFragment {
    override
    def onCreateDialog(savedInstanceState: Bundle) : Dialog = {
        new DatePickerDialog(
            getActivity(),
            listener,
            this.default_date.getYear(),
            this.default_date.getMonthOfYear() - 1, // android uses zero indexed months
            this.default_date.getDayOfMonth()
        )
    }
}
