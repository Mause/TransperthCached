package com.lysdev.transperthcached.activities;

import java.util.List;
import java.util.ArrayList;

import android.view.KeyEvent;

import android.database.Cursor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.database.FavouriteStopDatabaseHelper;
import com.lysdev.transperthcached.ui.FavouriteStopArrayAdapter;
import com.lysdev.transperthcached.ui.FavouriteStopInputFragment;

import com.lysdev.transperthcached.models.FavouriteStop;


public class FavouriteStopsActivity extends FragmentActivity
                                 implements
                                            // DialogInterface.OnClickListener,
                                            FavouriteStopInputFragment.OnFavouriteStopAddListener,
                                            AdapterView.OnItemClickListener,
                                            FavouriteStopArrayAdapter.OnDeleteListener {

    private Cursor stops_cursor;
    private FavouriteStopArrayAdapter stops_adapter;
    private FavouriteStopDatabaseHelper db;
    private ListView stops;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_stops);

        db = new FavouriteStopDatabaseHelper(this);

        List<FavouriteStop> stop_db = db.getFavouriteStops();
        if (stop_db == null) {
            Log.d("TransperthCached", "Got null from db");
            stop_db = new ArrayList<FavouriteStop>();
        }

        stops = (ListView) findViewById(R.id.favourite_stops);
        stops_adapter = new FavouriteStopArrayAdapter(this, stop_db);
        stops.setAdapter(stops_adapter);
        stops.setOnItemClickListener(this);
        stops_adapter.setOnDeleteListener(this);
    }

    public void onDelete(FavouriteStop fav) {
        Log.d("TransperthCached", "Delete: " + fav.toString());
        db.deleteStop(fav);
        updateStops();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FavouriteStop selected_stop = (FavouriteStop)parent.getItemAtPosition(position);

        Log.d("TransperthCached", "Selected stop: " + selected_stop.toString());

        // http://stackoverflow.com/questions/19286970/using-intents-to-pass-data-between-activities-in-android
        startActivity(
            new Intent(this, MainActivity.class)
            .putExtra("stop_num", selected_stop.getStopNumber())
        );
    }

    public void deleteButtonClicked(View view) {}
    public void    addButtonClicked(View view) { addButtonClicked(); }
    public void    addButtonClicked() {
        new FavouriteStopInputFragment(this)
        .show(getSupportFragmentManager(), "favouriteStopInput");
    }

    public void onFavouriteStopAdd(String stop_number, DialogInterface dialog) {
        if (stop_number.length() == 5) {
            Log.d("TransperthCached", "Stop number: " + stop_number);

            int istop_number = Integer.parseInt(stop_number);
            if (db.stopExists(istop_number)) {
                Toast.makeText(
                    this,
                    R.string.favourite_stop_exists,
                    Toast.LENGTH_LONG
                ).show();
            } else {
                db.addFavouriteStop(new FavouriteStop(istop_number));

                updateStops();
            }

            dialog.dismiss();
        } else {
            Log.d("TransperthCached", "Not long enough");

            Toast.makeText(
                this,
                R.string.stop_number_not_long_enough,
                Toast.LENGTH_LONG
            ).show();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_MENU) {
            return super.onKeyUp(keyCode, event);
        }

        addButtonClicked();
        return true;
    }

    private void updateStops() {
        stops_adapter.clear();
        for (FavouriteStop stop : db.getFavouriteStops()) {
            stops_adapter.add(stop);
        }
        stops_adapter.notifyDataSetChanged();
    }

    // protected void onSaveInstanceState(Bundle outState) {
    //     outState.putString("stop_number", stop_num_widget.getText().toString());
    // }
}
