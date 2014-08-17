package com.lysdev.transperthcached.activities;

import java.util.ArrayList;
import android.widget.AdapterView;
import android.database.Cursor;
import android.app.TabActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.text.InputFilter;
import android.text.InputType;

import android.view.Gravity;
import android.view.View;

import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lysdev.transperthcached.R;



class FavouriteStop {
    private String stop_number;

    public FavouriteStop(String stop_number) {
        this.stop_number = stop_number;
    }

    public String getStopNumber() {
        return this.stop_number;
    }

    public String toString() {
        return this.stop_number;
    }
}


public class FavouriteStopsActivity extends FragmentActivity
                                 implements DialogInterface.OnClickListener,
                                            AdapterView.OnItemClickListener {

    private EditText stopInput;
    private ListView stops;
    private ArrayAdapter<FavouriteStop> stops_adapter;
    private Cursor stops_cursor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_stops);

        stops = (ListView) findViewById(R.id.favourite_stops);
        stops_adapter = new ArrayAdapter<FavouriteStop>(
            this,
            android.R.layout.simple_list_item_1,
            // R.id.chk,
            new ArrayList<FavouriteStop>()
        );
        stops.setAdapter(stops_adapter);
        stops.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FavouriteStop selected_stop = (FavouriteStop)parent.getItemAtPosition(position);

        Log.d("TransperthCached", "Selected stop: " + selected_stop.toString());

        TabActivity acparent = (TabActivity) getParent();
        acparent.getTabHost().setCurrentTab(2);
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

            stops_adapter.add(
                new FavouriteStop(stop_number)
            );

            dialog.dismiss();
        } else {
            Log.d("TransperthCached", "Not long enough");

            Toast.makeText(
                this,
                "Stop numbers must be five digits long",
                Toast.LENGTH_LONG
            );
        }
    }

    // protected void onSaveInstanceState(Bundle outState) {
    //     outState.putString("stop_number", stop_num_widget.getText().toString());
    // }
}
