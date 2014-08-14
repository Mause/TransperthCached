package com.lysdev.transperthcached.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.silverrails.NearbyTransitStop;


class SelectStopDialogItem {
    NearbyTransitStop stop;

    public SelectStopDialogItem(NearbyTransitStop stop) {
        this.stop = stop;
    }

    public String toString() {
        return String.format(
            "%s %d metres away",
            stop.getDescription(),
            stop.getDistance()
        );
    }
}


public class SelectStopDialog extends DialogFragment {
    ArrayList<NearbyTransitStop> stops;
    SelectStopDialogOnSelected callback;

    public SelectStopDialog(
            ArrayList<NearbyTransitStop> stops,
            SelectStopDialogOnSelected callback) {
        this.stops = stops;
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView modeList = new ListView(getActivity());
        final Dialog dialog = new AlertDialog.Builder(getActivity())
            .setTitle("Select stop")
            .setView(modeList)
            .create();

        ArrayAdapter<SelectStopDialogItem> modeAdapter = new ArrayAdapter<SelectStopDialogItem>(
            getActivity(),
            R.layout.select_stop_item
            // android.R.layout.simple_list_item_1,
            // android.R.id.text1
        );
        for (NearbyTransitStop s : stops) {
            modeAdapter.add(new SelectStopDialogItem(s));
        }
        modeList.setAdapter(modeAdapter);

        modeList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();

                    NearbyTransitStop selected_stop = (
                        (SelectStopDialogItem)parent.getItemAtPosition(position)
                    ).stop;

                    Log.d("TransperthCached", "selected: " + selected_stop.toString());
                    callback.onSelected(selected_stop);
                }
            }
        );

        return dialog;
    }
}
