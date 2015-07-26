package com.lysdev.transperthcached.activities.train;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lysdev.transperthcached.activities.train.Direction;
import com.lysdev.transperthcached.R;


public class TrainStationSelectActivity extends Activity
                                        implements OnItemClickListener {
    String line_name;
    Direction direction;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);

        this.line_name = getIntent().getStringExtra("line_name");
        this.direction = Direction.from_val("direction", getIntent());

        List<String> stations = new ArrayList<String>();
        try {
            JSONObject json = Stations.loadJSON(this);
            JSONArray arr = json.getJSONArray(this.line_name);
            for (int i=0; i<arr.length(); i++) {
                stations.add(arr.getString(i));
            }
        } catch (JSONException e) {
            Log.e("TransperthCached", "JSONException", e);
        }

        Log.d("TransperthCached", String.format("Line: %s", line_name));
        ListView stations_lv = (ListView) findViewById(R.id.stations);
        ListAdapter ad = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            stations
        );
        stations_lv.setAdapter(ad);
        stations_lv.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String station_name = ((TextView)view).getText().toString();

        Log.d(
            "TransperthCached",
            String.format(
                "%s - %s - %s",
                this.direction.toString(),
                this.line_name,
                station_name
            )
        );

        Intent intent = new Intent(this, TrainStationTimesActivity.class);
        intent.putExtra("direction", this.direction.ordinal());
        intent.putExtra("line_name", this.line_name);
        intent.putExtra("station_name", station_name);
        startActivity(intent);
    }
}
