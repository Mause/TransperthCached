package com.lysdev.transperthcached.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lysdev.transperthcached.R;


public class FavouriteStopInputFragment extends DialogFragment {
    private OnFavouriteStopAddListener listener;

    public FavouriteStopInputFragment(OnFavouriteStopAddListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // EditText stopInput = new EditText(this);
        // stopInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
        // stopInput.setHint(R.string.stop_number_hint);
        // stopInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        // stopInput.setGravity(
        //     Gravity.CENTER_VERTICAL |
        //     Gravity.CENTER
        // );
        final View layout = getActivity().getLayoutInflater().inflate(R.layout.favourite_stop_input, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add favourite stop")
            .setCancelable(true)
            // .setView(stopInput)
            .setView(layout)
            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    EditText stopInput = (EditText) layout.findViewById(R.id.stop_number);
                    String stop_number = stopInput.getText().toString();

                    FavouriteStopInputFragment.this.listener.onFavouriteStopAdd(
                        stop_number,
                        dialog
                    );
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        return builder.create();
    }

    public interface OnFavouriteStopAddListener {
        public void onFavouriteStopAdd(String stop_number, DialogInterface dialog);
    }
}
