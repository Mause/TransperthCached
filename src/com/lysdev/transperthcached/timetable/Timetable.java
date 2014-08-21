package com.lysdev.transperthcached.timetable;

import java.util.HashMap;
import java.io.IOException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Timetable {
    public static final int WEEKDAYS = 0x1;
    public static final int SATURDAY = 0x2;
    public static final int SUNDAY   = 0x3;

    private HashMap<String, StopTimetable> _cache;
    private DatabaseHelper helper;

    public Timetable() {
        _cache = new HashMap<String, StopTimetable>();
    }

    public void onDestroy() {
        if (this.helper != null)
            this.helper.close();
    }

    public void initialize(Context context) throws IOException {
        helper = new DatabaseHelper(context);

            helper.createDataBase();

        try {
            helper.openDataBase();
        } catch (SQLException sqle) {
            Log.d("TransperthCached", "Caught SQLException");
            throw sqle;
        }
    }

    public StopTimetable getVisitsForStop(String stop_num) {
        if (stop_num == null) return null;

        // first check the in-memory cache
        StopTimetable cached_val = _cache.get(stop_num);
        if (cached_val != null) return cached_val;

        // otherwise hit the database
        return _getVisitsForStop_uncached(stop_num);
    }

    private StopTimetable _getVisitsForStop_uncached(String stop_num) {
        StopTimetable st = helper.getVisitsForStop(stop_num);

        if (st == null) return st;

        _cache.put(stop_num, st);
        Log.d("TransperthCached", "cached");

        return st;
    }
}
