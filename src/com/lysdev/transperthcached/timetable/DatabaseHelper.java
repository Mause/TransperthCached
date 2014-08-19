package com.lysdev.transperthcached.timetable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    // private static String DB_PATH = "/data/data/com.lysdev.transperthcached/databases/";
    private static String DB_PATH = "/sdcard/Android/data/com.lysdev.transperthcached/databases/";
    private static String DB_NAME = "transperthcache.db";

    private SQLiteDatabase myDataBase;

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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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

        myDataBase = SQLiteDatabase.openDatabase(
            myPath, null, SQLiteDatabase.OPEN_READONLY
        );

        return myDataBase;
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null) {
            myDataBase.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
}
