package com.lysdev.transperthcached.activities

import android.widget.Toast
import android.app.ProgressDialog

import android.app.TabActivity
import android.os.Bundle
import android.util.Log
import android.os.Looper

import android.content.Intent

import android.widget.TabHost

import com.lysdev.transperthcached.R
import com.lysdev.transperthcached.utils.Util
import com.lysdev.transperthcached.timetable.DatabaseHelper


object MainActivity {
    var instance : DatabaseHelper = null
    def getConstantDB() : DatabaseHelper = {
        MainActivity.instance
    }
}


class MainActivity extends TabActivity
                   with TabHost.OnTabChangeListener {

    def tab(intent: Intent, tag: String, resource_id: Int) {
        getTabHost().addTab(
            getTabHost()
            .newTabSpec(tag)
            .setIndicator("", getResources().getDrawable(resource_id))
            .setContent(intent)
        )
    }

    def tab(cls: Class[_], tag: String, resource_id: Int) {
        tab(new Intent(this, cls), tag, resource_id)
    }

    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val tabHost = getTabHost()

        tabHost.setOnTabChangedListener(this)

        tab(classOf[TrainActivity],          "Train",          R.drawable.icon_train_config)
        tab(classOf[FavouriteStopsActivity], "FavouriteStops", R.drawable.icon_star_config)

        val intent = new Intent(this, classOf[StopTimetableActivity])
        val stop_num = getIntent().getIntExtra("stop_num", 0)
        if (stop_num != 0) {
            Log.d("TransperthCached", "Got bundled stop_num: " + stop_num)
            intent.putExtra("stop_num", stop_num)
        }
        tab(intent, "StopTimetable", R.drawable.icon_timetable_config)

        if (stop_num == 0) tabHost.setCurrentTab(0)
        else                  tabHost.setCurrentTab(2)

        val mDialog = new ProgressDialog(this)
        mDialog.setMessage("Loading...")
        mDialog.setCancelable(false)
        mDialog.show()

        new Thread() {
            override def run() {
                Looper.prepare()

                MainActivity.this.initializeDB()

                mDialog.dismiss()
                Log.d("TransperthCached", "initialized")
            }
        }.start()
    }

    def db_error(mDialog: ProgressDialog, e: Throwable) {
        Log.e("TransperthCached", "Couldn't initialize database", e)
        mDialog.dismiss()
        Toast.makeText(
            MainActivity.this,
            "Could not initialize database",
            Toast.LENGTH_LONG
        ).show()
    }

    def initializeDB() {
        MainActivity.instance = DatabaseHelper()

        MainActivity.instance.createDataBase()

        try {
            MainActivity.instance.openDataBase()
        } catch {
            case sqle: SQLException => {
                Log.d("TransperthCached", "Caught SQLException")
                throw sqle
            }
        }
    }

    override
    def onDestroy() {
        super.onDestroy()

        if (MainActivity.instance != null)
            MainActivity.instance.close()
    }

    def onTabChanged(tabID: String) {
        Util.hideSoftKeyboard(this)
    }
}
