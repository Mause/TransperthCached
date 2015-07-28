package com.lysdev.transperthcached.ui

import android.app.AlertDialog
import android.app.Dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.View

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import com.lysdev.transperthcached.R
import com.lysdev.transperthcached.silverrails.NearbyTransitStop

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._


class SelectStopDialogItem(stop: NearbyTransitStop) {
    def getStop() = this.stop
    override def toString() = "${stop.getDescription} ${stop.getDistance} metres away"
}


class SelectStopDialog(stops: List[NearbyTransitStop],
                       callback: (NearbyTransitStop => Unit))
                      extends DialogFragment {

    override
    def onCreateDialog(savedInstanceState: Bundle) : Dialog = {
        val modeList = new ListView(getActivity())
        val dialog = new AlertDialog.Builder(getActivity())
            .setTitle("Select stop")
            .setView(modeList)
            .create()

        val modeAdapter = new ArrayAdapter[SelectStopDialogItem](
            getActivity(),
            R.layout.select_stop_item
            // android.R.layout.simple_list_item_1,
            // android.R.id.text1
        )
        modeAdapter.addAll(
            stops
            .map(new SelectStopDialogItem(_))
            .asJavaCollection
        )
        modeList.setAdapter(modeAdapter)

        modeList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
                    dialog.dismiss()

                    val selected_stop = (
                        parent
                        .getItemAtPosition(position)
                        .asInstanceOf[SelectStopDialogItem]
                        .getStop()
                    )

                    Log.d("TransperthCached", "selected: " + selected_stop.toString())
                    callback(selected_stop)
                }
            }
        )

        dialog
    }
}
