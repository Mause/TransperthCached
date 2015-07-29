package com.lysdev.transperthcached.timetable

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log

import org.scaloid.common._
import scala.io.Source
import scala.collection.immutable.{Map => IMap}

object DatabaseHelper {
    val DB_NAME = "transperthcache.db"
}

class DatabaseHelper()(implicit context: Context) extends SQLiteOpenHelper(context, DatabaseHelper.DB_NAME, null, 1) {

    var db : SQLiteDatabase = null
    lazy val db_path = (
        new File(
            Environment.getExternalStorageDirectory(),
            "Android/data/com.lysdev.transperthcached/databases/"
        )
    )

    def createDataBase() {
        if (!checkDataBase()) {
            Log.d("TransperthCached", "Copying database")

            this.getReadableDatabase()

            try {
                copyDataBase()
            } catch {
                case e: IOException => throw new Error(
                    "Error copying database: " +
                    e.toString()
                )
            }
        }
    }

    def checkDataBase() : Boolean = {
        var checkDB : SQLiteDatabase = null
        val dbPath = new File(db_path, DatabaseHelper.DB_NAME)

        if (!dbPath.exists()) {
            Log.d("TransperthCached", "Database does not yet exist")
            return false
        }

        try {
            checkDB = SQLiteDatabase.openDatabase(
                dbPath.getPath(),
                null,
                SQLiteDatabase.OPEN_READONLY
            )
        } catch {
            case e: SQLiteException => Log.d("TransperthCached", "SQLiteException whilst testing db")
        }

        if (checkDB != null) {
            checkDB.close()
        }

        checkDB != null
    }

    def copyDataBase() {
        // Open your local db as the input stream
        val myInput = Source.fromInputStream(context.getAssets().open(DatabaseHelper.DB_NAME))

        // Path to the just created empty db
        val outFileName = new File(db_path, DatabaseHelper.DB_NAME).getPath()

        val folder = db_path
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // Open the empty db as the output stream
        val myOutput = new FileOutputStream(outFileName)

        // transfer bytes from the inputfile to the outputfile
        myInput.foreach(myOutput.write(_))

        // Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

        new File(db_path, "/.nomedia").createNewFile()
    }

    // Open the database
    def openDataBase() : SQLiteDatabase = {
        val myPath = new File(db_path, DatabaseHelper.DB_NAME).getPath()

        db = SQLiteDatabase.openDatabase(
            myPath, null, SQLiteDatabase.OPEN_READONLY
        )

        db
    }

    override
    def close() = {
        synchronized {
            if(db != null) {
                db.close()
            }

            super.close()
        }
    }

    override
    def onCreate(db: SQLiteDatabase) {}

    override
    def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return db.query(....)" so it'd be easy
    // to you to create adapters for your views.

    def build_result(cursor: Cursor) : Visit = {
        val time_hour = cursor.getInt(3)
        val time_minute = cursor.getInt(4)

        val stop_num = cursor.getInt(0)
        val route_num = cursor.getInt(2)

        Visit.fromRaw(
            stop_num,
            route_num,
            time_hour,
            time_minute
        )
    }

    def getVisitsForStop(stop_num: Int) : StopTimetable = {
        Log.d("TransperthCached", "getting results from db")

        assert ((
            "/sdcard/Android/data/" +
            "com.lysdev.transperthcached/databases" +
            "/transperthcache.db").equals(db.getPath())
        )

        val cursor = db.query(
            "visit",              // table
            Array[String]( "*" ), // selected fields
            "stop_num=?",         // where clause
            Array[String] ( String.valueOf(stop_num) ),  // where fields
            null, // having
            null, // orderBy
            null  // limit
        )

        val stops : IMap[Int, List[Visit]] = (
            cursor
            .map(c => (c.getInt(1), build_result(c)))
            .toSeq
            .groupBy(_._1)   // group by day type
            .mapValues(_.map(_._2).toList) // get actual visit
        )

        if (stops.values.nonEmpty) {
            new StopTimetable(
                stop_num,

                stops(Timetable.WEEKDAYS),
                stops(Timetable.SATURDAY),
                stops(Timetable.SUNDAY)
            )
        } else {
            null
        }
    }

    def getDescriptionOfStop(stop_number: Int) : String = {
        val cursor = db.query(
            "stops",
            Array[String]( "description" ),
            "stop_num=?",
            Array[String]( String.valueOf(stop_number) ),
            null, null, null
        )

        cursor.toString(null)
    }

    def listTables() : List[String] = {
        val cursor = db.query(
            "sqlite_master",
            Array[String]( "name" ),
            "type=?",
            Array[String]( "table" ),
            null, null, null
        )

        cursor.map(_.getString(0)).toList
    }
}
