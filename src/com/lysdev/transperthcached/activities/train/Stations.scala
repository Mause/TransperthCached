package com.lysdev.transperthcached.activities.train

import android.content.Context
import play.api.libs.json._
import com.lysdev.transperthcached.utils.Util

object Stations {
    def loadJSON(ctx: Context) : JsValue = {
        val myInput = ctx.getAssets.open("train_stations.json")

        Json.parse(Util.convertStreamToString(myInput))
    }

    def lineNames(ctx: Context) = {
        loadJSON(ctx).as[JsObject].keys
    }

    def stationNames(ctx: Context, line_name: String) = {
        (loadJSON(ctx) \ line_name).as[Seq[String]]
    }
}
