package com.lysdev.transperthcached.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class OkDialog extends DialogFragment {
    public OkDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title =   args.getString("title"),
               message = args.getString("message");

        title = title == null ? "" : title;
        message = message == null ? "" : message;

        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                android.R.string.ok,
                new OkDialogOnClickListen()
            ).create();
    }
}

class OkDialogOnClickListen implements DialogInterface.OnClickListener {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
    }
}
