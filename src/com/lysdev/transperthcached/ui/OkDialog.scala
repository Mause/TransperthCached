package com.lysdev.transperthcached.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment


class OkDialog extends DialogFragment {
    override
    def onCreateDialog(savedInstanceState: Bundle) : Dialog = {
        val args = getArguments()
        val title =   args.getString("title", "")
        val message = args.getString("message", "")

        (
            new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                android.R.string.ok,
                new OkDialogOnClickListen()
            )
            .create()
        )
    }
}

class OkDialogOnClickListen extends DialogInterface.OnClickListener {
    override
    def onClick(dialog: DialogInterface, which: Int) {
        // getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null)
    }
}
