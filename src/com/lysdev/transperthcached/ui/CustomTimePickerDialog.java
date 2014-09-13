package com.lysdev.transperthcached.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.joda.time.LocalTime;


public class CustomTimePickerDialog extends TimePickerDialog {
    public CustomTimePickerDialog(
            Context context,
            TimePickerDialog.OnTimeSetListener callBack,
            int hourOfDay, int minute,
            boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Button neutral = getButton(TimePickerDialog.BUTTON_NEUTRAL);

        neutral.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    LocalTime now = LocalTime.now();
                    updateTime(
                        now.getHourOfDay(),
                        now.getMinuteOfHour()
                    );

                    getButton(TimePickerDialog.BUTTON_POSITIVE).performClick();
                }
            }
        );

        neutral.setText("Now");
        neutral.setVisibility(View.VISIBLE);
    }
}
