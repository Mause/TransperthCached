package com.lysdev.transperthcached.activities.train

import android.content.Context
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

import org.json._

import com.lysdev.transperthcached.utils.Util


object OrgJsonImprovments {
    implicit class BetterJSONArray(val arr: JSONArray) {
        val lookup = Map[Class[_], (JSONArray, Int) => _](
            classOf[Boolean]    -> ((arr: JSONArray, i: Int) => arr.getBoolean(i)),
            // classOf[Class]      -> ((arr: JSONArray, i: Int) => arr.getClass(i)),
            classOf[Double]     -> ((arr: JSONArray, i: Int) => arr.getDouble(i)),
            classOf[Int]        -> ((arr: JSONArray, i: Int) => arr.getInt(i)),
            classOf[JSONArray]  -> ((arr: JSONArray, i: Int) => arr.getJSONArray(i)),
            classOf[JSONObject] -> ((arr: JSONArray, i: Int) => arr.getJSONObject(i)),
            classOf[Long]       -> ((arr: JSONArray, i: Int) => arr.getLong(i)),
            classOf[String]     -> ((arr: JSONArray, i: Int) => arr.getString(i))
        )

        def toList[T](item_type: Class[T]) : Seq[T] = {
            (
                for (i <- 0 to this.arr.length() - 1)
                    yield lookup(item_type)(this.arr, i)
            ).toSeq.asInstanceOf[Seq[T]]
        }
    }
}

import OrgJsonImprovments._

object Stations {
    def loadJSON(ctx: Context) = {
        val myInput = ctx.getAssets.open("train_stations.json")

        new JSONObject(Util.convertStreamToString(myInput))
    }

    def lineNames()(implicit ctx: Context) : Seq[String] = {
        loadJSON(ctx).keys.asScala.toSeq.asInstanceOf[Seq[String]]
    }

    def stationNames(line_name: String)(implicit ctx: Context) : Option[Seq[String]] = {
        Option(loadJSON(ctx).optJSONArray(line_name)).map(
            _.toList(classOf[String])
        )
    }
}
