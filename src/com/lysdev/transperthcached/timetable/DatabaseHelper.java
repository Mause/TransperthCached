package com.lysdev.transperthcached.timetable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/sdcard/Android/data/com.lysdev.transperthcached/databases/";
    private static String DB_NAME = "transperthcache.db";

    private SQLiteDatabase db;

    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws Error {
        if (!checkDataBase()) {
            Log.d("TransperthCached", "Copying database");

            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error(
                    "Error copying database: " +
                    e.toString()
                );
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;

        File dbFile = new File(myPath);
        if (!dbFile.exists()) return false;

        try {
            checkDB = SQLiteDatabase.openDatabase(
                myPath,
                null,
                SQLiteDatabase.OPEN_READONLY
            );
        } catch (SQLiteException e) {}

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        File folder = new File(DB_PATH);
        if (!folder.exists())
            folder.mkdirs();

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        new File(DB_PATH + "/.nomedia").createNewFile();
    }

    // Open the database
    public SQLiteDatabase openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;

        db = SQLiteDatabase.openDatabase(
            myPath, null, SQLiteDatabase.OPEN_READONLY
        );

        return db;
    }

    @Override
    public synchronized void close() {
        if(db != null) {
            db.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return db.query(....)" so it'd be easy
    // to you to create adapters for your views.

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

    public StopTimetable getVisitsForStop(String stop_num) {
        Log.d("TransperthCached", "getting results from db");

        // SQLiteDatabase db = getReadableDatabase();

        Log.d("TransperthCached", "DB Path: " + db.getPath());

        assert ((
            "/sdcard/Android/data/" +
            "com.lysdev.transperthcached/databases" +
            "/transperthcache.db").equals(db.getPath())
        );

        Cursor cursor = db.query(
            "visit",              // table
            new String[] { "*" }, // selected fields
            "stop_num=?",         // where clause
            new String[] { String.valueOf(stop_num) },  // where fields
            null, // having
            null, // orderBy
            null  // limit
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        Vector<Visit> weekdays = new Vector<Visit>(),
                      saturday = new Vector<Visit>(),
                      sunday   = new Vector<Visit>();

        int num_results = 0;
        while (!cursor.isAfterLast()) {
            int visit_day_type = cursor.getInt(1);
            Visit result = build_result(cursor);

            switch(visit_day_type) {
                case Timetable.WEEKDAYS:  weekdays.add(result);   break;
                case Timetable.SATURDAY:  saturday.add(result);   break;
                case Timetable.SUNDAY:    sunday.add(result);     break;
                default:        throw new java.lang.Error("Bad visit type");
            }
            num_results++;
            cursor.moveToNext();
        }
        Log.d("TransperthCached", num_results + " results");

        if (num_results == 0) {
            // ie, no such stop
            return null;
        }

        return new StopTimetable(
            stop_num,

            weekdays,
            saturday,
            sunday
        );
    }

    public String getDescriptionOfStop(String stop_number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "stops",
            new String[] { "Description" },
            "Code=?",
            new String[] { stop_number },
            null, null, null
        );

        if (cursor == null) return null;

        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public ArrayList<String> listTables() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "sqlite_master",
            new String[] { "name" },
            "type=?",
            new String[] { "table" },
            null, null, null
        );

        if (cursor == null) return null;

        cursor.moveToFirst();

        ArrayList<String> tables = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            tables.add(cursor.getString(0));
        }

        return tables;
    }
}
