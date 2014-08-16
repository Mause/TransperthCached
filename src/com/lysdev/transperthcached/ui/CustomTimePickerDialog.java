package com.lysdev.transperthcached.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.joda.time.LocalTime;
import com.lysdev.transperthcached.R;


public class CustomTimePickerDialog extends TimePickerDialog {
    public CustomTimePickerDialog(
            Context context,
            TimePickerDialog.OnTimeSetListener callBack,
            int hourOfDay, int minute,
            boolean is24HourView) {
        super(
            context,
            callBack,
            hourOfDay,
            minute,
            is24HourView
        );

        setButton(TimePickerDialog.BUTTON_NEUTRAL, "Now", this);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getButton(TimePickerDialog.BUTTON_NEUTRAL).setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {

                    LocalTime now = LocalTime.now();
                    CustomTimePickerDialog.this.updateTime(
                        now.getHourOfDay(),
                        now.getMinuteOfHour()
                    );

                    // CustomTimePickerDialog.this.dismiss();

                    Log.d("TransperthCached", "Neutral button clicked");
                }
            }
        );
    }
}
