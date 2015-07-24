package com.lysdev.transperthcached.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lysdev.transperthcached.activities.train.TrainLineSelectActivity;
import com.lysdev.transperthcached.activities.train.Direction;
import com.lysdev.transperthcached.R;

public class TrainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trains);
    }

    public void onClickDirection(View viewery) {
        Button button = (Button)viewery;
        Direction dir = (
            button.getId() == R.id.from_perth ?
            Direction.FROM :
            Direction.TO
        );

        Intent intent = new Intent(this, TrainLineSelectActivity.class);
        intent.putExtra("direction", dir.ordinal());
        startActivity(intent);
    }
}
