package com.lysdev.transperthcached.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;

import com.lysdev.transperthcached.models.FavouriteStop;


public class FavouriteStopDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "FavouriteStopDatabase";

    public FavouriteStopDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public void onUpgrade(SQLiteDatabase db, int a, int b) {}
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS favourite_stops (" +
            "    sid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    stop_number TEXT" +
            ");"
        );
    }

    public void addFavouriteStop(FavouriteStop toAdd) {
        ContentValues values = new ContentValues();
        values.put("stop_number", toAdd.getStopNumber());

        getWritableDatabase().insert("favourite_stops", null, values);
    }

    public boolean stopExists(FavouriteStop stop) {
        return stopExists(stop.getStopNumber());
    }

    public boolean stopExists(int stop_number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",
            new String[] { "count(*)" },
            "stop_number=?",
            new String[] { String.valueOf(stop_number) },
            null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return false;
        }

        return cursor.getInt(0) > 0;
    }

    public void deleteStop(FavouriteStop stop) {
        deleteStop(stop.getStopNumber());
    }

    public void deleteStop(int stop_number) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
            "favourite_stops",
            "stop_number=?",
            new String[] { String.valueOf(stop_number) }
        );
    }

    public ArrayList<FavouriteStop> getFavouriteStops() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",    // table
            new String[] { "*" }, // selected fields
            null, null, null, null, null
        );

        ArrayList<FavouriteStop> stops = new ArrayList<FavouriteStop>();

        if (cursor == null || !cursor.moveToFirst()) {
            return stops;
        }

        while (!cursor.isAfterLast()) {
            stops.add(
                new FavouriteStop(
                    cursor.getInt(0),
                    cursor.getInt(1)
                )
            );

            cursor.moveToNext();
        }

        return stops;
    }
}
