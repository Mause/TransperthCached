package com.lysdev.transperthcached.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TrainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the train tab");
        setContentView(textview);
    }
}
