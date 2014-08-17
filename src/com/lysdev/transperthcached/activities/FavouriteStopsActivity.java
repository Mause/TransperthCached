package com.lysdev.transperthcached.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FavouriteStopsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the favourite stops tab");
        setContentView(textview);
    }
}
