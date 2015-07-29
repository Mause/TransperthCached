package com.lysdev.transperthcached.ui

import java.util.List
import android.widget.TextView

import android.content.Context
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.View
import android.widget.Button

import com.lysdev.transperthcached.R
import com.lysdev.transperthcached.models.FavouriteStop

import org.scaloid.common._


class FavouriteStopArrayAdapter(list: List[FavouriteStop])
                               (implicit context: Context)
                            extends ArrayAdapter[FavouriteStop](context,
                                                                R.layout.favourite_stop_item,
                                                                R.id.description,
                                                                list) {

    override
    def isEnabled(position: Int) = true

    var onDelete : (FavouriteStop => Unit) = null
    def setOnDeleteListener(listener: (FavouriteStop => Unit)) {
        this.onDelete = listener
    }

    override
    def getView(position: Int, convertView: View, parent: ViewGroup) : View = {
        val view = super.getView(position, convertView, parent)

        val item = getItem(position)

        if (item != null) {
            val stop_number = view.findViewById(R.id.stop_number).asInstanceOf[TextView]
            if (stop_number != null) {
                stop_number.setText(
                    String.valueOf(item.getStopNumber)
                )
            }

            val description = view.findViewById(R.id.description).asInstanceOf[TextView]
            if (description != null) {
                description.setText(item.getDescription)
            }
        }

        val deleteBtn = view.findViewById(R.id.delete_btn).asInstanceOf[Button]
        deleteBtn.onClick((v: View) => {
            if (onDelete == null) {
                throw new IllegalArgumentException("you must set the delete listener")
            }
            onDelete(getItem(position))
        })

        view
    }
}
