package com.lysdev.transperthcached.activities;


import android.database.Cursor;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.text.InputFilter;
import android.text.InputType;

import android.view.Gravity;
import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.database.FavouriteStopDatabaseHelper;
import com.lysdev.transperthcached.ui.FavouriteStopArrayAdapter;

import com.lysdev.transperthcached.models.FavouriteStop;


public class FavouriteStopsActivity extends FragmentActivity
                                 implements DialogInterface.OnClickListener,
                                            AdapterView.OnItemClickListener,
                                            FavouriteStopArrayAdapter.OnDeleteListener {

    private Cursor stops_cursor;
    private FavouriteStopArrayAdapter stops_adapter;
    private EditText stopInput;
    private FavouriteStopDatabaseHelper db;
    private ListView stops;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_stops);

        db = new FavouriteStopDatabaseHelper(this);

        stops = (ListView) findViewById(R.id.favourite_stops);
        stops_adapter = new FavouriteStopArrayAdapter(
            this,
            R.layout.favourite_stop_item,
            db.getFavouriteStops()
        );
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

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("stop_num", selected_stop.getStopNumber());
        startActivity(intent);
    }

    public void deleteButtonClicked(View view) {}
    public void    addButtonClicked(View view) {
        stopInput = new EditText(this);
        stopInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
        stopInput.setHint(R.string.stop_number_hint);
        stopInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        stopInput.setGravity(
            Gravity.CENTER_VERTICAL |
            Gravity.CENTER
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add favourite stop")
            .setCancelable(true)
            .setView(stopInput)
            .setPositiveButton("Add", this)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        builder.create().show();
    }

    public void onClick(DialogInterface dialog, int id) {
        String stop_number = stopInput.getText().toString();

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
