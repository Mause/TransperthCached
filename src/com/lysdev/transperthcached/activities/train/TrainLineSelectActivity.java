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

import org.json.JSONException;
import org.json.JSONObject;

import com.lysdev.transperthcached.activities.train.Direction;
import com.lysdev.transperthcached.R;

public class TrainLineSelectActivity extends Activity
                                     implements OnItemClickListener {
    Direction direction;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_stations);

        this.direction = Direction.from_val("direction", getIntent());

        List<String> lines = null;
        try {
            lines = parseJSON();
        } catch (JSONException ioe) {
            Log.e("TransperthCached", "Couldn't parse json", ioe);
            return;
        }

        ListAdapter ad = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            lines
        );

        setContentView(R.layout.train_stations);
        ListView stations = (ListView) findViewById(R.id.stations);

        stations.setAdapter(ad);
        stations.setOnItemClickListener(this);
    }

    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TrainStationSelectActivity.class);
        intent.putExtra("direction", this.direction.ordinal());
        intent.putExtra("line_name", (((TextView)view).getText().toString()));
        startActivity(intent);
    }

    public List<String> fromIterator(Iterator<String> iter) {
        List<String> lst = new ArrayList<String>();
        while (iter.hasNext())
            lst.add(iter.next());
        return lst;
    }

    List<String> parseJSON() throws JSONException {
        JSONObject json = Stations.loadJSON(this);

        return new ArrayList<String>(fromIterator(json.keys()));
    }
}
