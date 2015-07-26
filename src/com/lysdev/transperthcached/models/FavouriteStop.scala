package com.lysdev.transperthcached.models

import com.lysdev.transperthcached.activities.MainActivity

class FavouriteStop(val sid: Int, val stop_number: Int) {
    def this(stop_number: Integer) {
        this(0, stop_number)
    }

    lazy val description = (
        MainActivity.getConstantDB().getDescriptionOfStop(this.stop_number)
    )

    def getStopNumber = this.stop_number
    def getDescription = this.description

    // override def toString : String = String.format("%s - %s", this.stop_number)
}
