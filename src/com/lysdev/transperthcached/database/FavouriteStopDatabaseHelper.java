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
    private SQLiteDatabase db; // purely for use within onCreate; don't use it anywhere else!

    public FavouriteStopDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);

        // purely for use within onCreate; don't use it anywhere else!
        db = getWritableDatabase();
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

        db.insert("favourite_stops", null, values);
    }

    public boolean stopExists(FavouriteStop stop) {
        return stopExists(stop.getStopNumber());
    }

    public boolean stopExists(String stop_number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",
            new String[] { "count(*)" },
            "stop_number=?",
            new String[] { stop_number },
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

    public void deleteStop(String stop_number) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
            "favourite_stops",
            "stop_number=?",
            new String[] { stop_number }
        );
    }

    public ArrayList<FavouriteStop> getFavouriteStops() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",    // table
            new String[] { "*" }, // selected fields
            null, null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        ArrayList<FavouriteStop> stops = new ArrayList<FavouriteStop>();

        while (!cursor.isAfterLast()) {
            stops.add(
                new FavouriteStop(
                    cursor.getInt(0),
                    cursor.getString(1)
                )
            );

            cursor.moveToNext();
        }

        return stops;
    }
}
