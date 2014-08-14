package com.lysdev.transperthcached.timetable;

import java.util.HashMap;
import java.util.Vector;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Timetable {
    private static final int WEEKDAYS = 0x1;
    private static final int SATURDAY = 0x2;
    private static final int SUNDAY   = 0x3;

    private HashMap<String, StopTimetable> _cache;
    private SQLiteDatabase db;

    public Timetable() {
        _cache = new HashMap<String, StopTimetable>();
    }

    public void onDestroy() {
        this.db.close();
    }

    public void initialize(Context context) throws IOException {
        DatabaseHelper helper = new DatabaseHelper(context);

        // try {
            helper.createDataBase();
        // } catch (IOException ioe) {
        //     throw new Error("Unable to create database");
        // }

        try {
            this.db = helper.openDataBase();
        } catch (SQLException sqle) {
            Log.d("TransperthCached", "Caught SQLException");
            throw sqle;
        }

        Log.d("TransperthCached", "Its open? "  + db.isOpen());
    }

    public StopTimetable getVisitsForStop(String stop_num) {
        if (stop_num == null) return null;

        // first check the in-memory cache
        StopTimetable cached_val = _cache.get(stop_num);
        if (cached_val != null) return cached_val;

        // otherwise hit the database
        return _getVisitsForStop_uncached(stop_num);
    }

    private Visit build_result(Cursor cursor) {
        String stop_num, time;
        int route_num, time_hour, time_minute;

        time_hour = cursor.getInt(3);
        time_minute = cursor.getInt(4);

        stop_num = cursor.getString(0);
        route_num = cursor.getInt(2);

        return Visit.fromRaw(
            stop_num,
            route_num,
            time_hour,
            time_minute
        );
    }

    private StopTimetable _getVisitsForStop_uncached(String stop_num) {
        Log.d("TransperthCached", "getting results from db");

        if (db == null)
            Log.d("TransperthCached", "Bad database");

        Cursor cursor = db.query(
            "visit",
            new String[] { "*" },
            "stop_num=?",
            new String[] { String.valueOf(stop_num) },
            null, // having
            null, // orderBy
            null  // limit
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }


        for (String column : cursor.getColumnNames()) {
            Log.d("TransperthCached", "column: " + column);
        }

        Vector<Visit> weekdays = new Vector<Visit>();
        Vector<Visit> saturday = new Vector<Visit>();
        Vector<Visit> sunday   = new Vector<Visit>();

        int num_results = 0;
        while (!cursor.isAfterLast()) {
            int visit_day_type = cursor.getInt(1);
            Visit result = build_result(cursor);

            switch(visit_day_type) {
                case WEEKDAYS:  weekdays.add(result);   break;
                case SATURDAY:  saturday.add(result);   break;
                case SUNDAY:    sunday.add(result);     break;
                default:        throw new java.lang.Error();
            }
            num_results++;
            cursor.moveToNext();
        }
        Log.d("TransperthCached", num_results + " results");

        StopTimetable st = new StopTimetable(
            stop_num,

            weekdays,
            saturday,
            sunday
        );
        _cache.put(stop_num, st);
        Log.d("TransperthCached", "cached");

        return st;
    }
}
