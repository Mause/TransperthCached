package com.lysdev.transperthcached.timetable;

import java.util.HashMap;

import android.util.Log;

import com.lysdev.transperthcached.activities.MainActivity;


public class Timetable {
    public static final int WEEKDAYS = 0x1;
    public static final int SATURDAY = 0x2;
    public static final int SUNDAY   = 0x3;

    private HashMap<Integer, StopTimetable> _cache;

    public Timetable() {
        _cache = new HashMap<Integer, StopTimetable>();
    }

    public StopTimetable getVisitsForStop(int stop_num) {
        // first check the in-memory cache
        StopTimetable cached_val = _cache.get(stop_num);
        if (cached_val != null) return cached_val;

        // otherwise hit the database
        return _getVisitsForStop_uncached(stop_num);
    }

    private StopTimetable _getVisitsForStop_uncached(int stop_num) {
        StopTimetable st = MainActivity.getConstantDB().getVisitsForStop(stop_num);

        if (st == null) return st;

        _cache.put(stop_num, st);
        Log.d("TransperthCached", "cached");

        return st;
    }
}
