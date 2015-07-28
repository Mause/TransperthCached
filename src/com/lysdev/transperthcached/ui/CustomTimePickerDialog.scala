package com.lysdev.transperthcached.ui

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.DialogInterface

import org.joda.time.LocalTime


class CustomTimePickerDialog
            (
                context: Context,
                callBack: TimePickerDialog.OnTimeSetListener,
                hourOfDay: Int, minute: Int,
                is24HourView: Boolean
            )
            extends TimePickerDialog(
                context,
                callBack,
                hourOfDay,
                minute,
                is24HourView
            ) {

    override
    def onCreate(bundle: Bundle) {
        super.onCreate(bundle)

        val neutral = getButton(DialogInterface.BUTTON_NEUTRAL)

        neutral.setOnClickListener(
            new View.OnClickListener() {
                def onClick(view: View) {
                    val now = LocalTime.now()
                    updateTime(
                        now.getHourOfDay(),
                        now.getMinuteOfHour()
                    )

                    getButton(DialogInterface.BUTTON_POSITIVE).performClick()
                }
            }
        )

        neutral.setText("Now")
        neutral.setVisibility(View.VISIBLE)
    }
}
