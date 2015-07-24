package com.lysdev.transperthcached.activities.train;

import android.util.Log;
import android.content.Context;

import java.io.InputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lysdev.transperthcached.utils.Util;

public class Stations {
    public static JSONObject loadJSON(Context ctx) throws JSONException {
        InputStream myInput = null;
        try {
            myInput = ctx.getAssets().open("train_stations.json");
        } catch (IOException ioe) {
            Log.e("TransperthCached", "Couldn't get station data", ioe);
            return null;
        }

        String json = "";
        try {
            json = Util.convertStreamToString(myInput);
        } catch (IOException ioe) {
            Log.e("TransperthCached", "Couldn't parse string to stream", ioe);
            return null;
        }

        return new JSONObject(json);
    }
}
