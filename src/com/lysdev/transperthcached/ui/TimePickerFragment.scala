package com.lysdev.transperthcached.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat

import org.joda.time.LocalTime


class TimePickerFragment(listener : TimePickerDialog.OnTimeSetListener,
                         default_time : LocalTime)
                        extends DialogFragment {

    override
    def onCreateDialog(savedInstanceState: Bundle) : Dialog = {
        new CustomTimePickerDialog(
            getActivity(),
            listener,
            this.default_time.getHourOfDay(),
            this.default_time.getMinuteOfHour(),
            DateFormat.is24HourFormat(getActivity())
        )
    }
}
