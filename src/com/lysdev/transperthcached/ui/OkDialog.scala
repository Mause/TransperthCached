package com.lysdev.transperthcached.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.{Context, DialogInterface}
import android.os.Bundle
import android.support.v4.app.{DialogFragment, FragmentActivity}


object OkDialog {
    def ok_dialog(title: String, message: String)(implicit context: Context) {
        val dialog = new OkDialog()

        val args = new Bundle()
        args.putString("title", title)
        args.putString("message", message)
        dialog.setArguments(args)

        // dialog.setTargetFragment(this, 0)
        dialog.show(
            context
            .asInstanceOf[FragmentActivity]
            .getSupportFragmentManager(),
            "TransperthCached"
        )
    }
}


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
