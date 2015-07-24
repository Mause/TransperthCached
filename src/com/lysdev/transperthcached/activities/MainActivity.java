package com.lysdev.transperthcached.activities;

import java.io.IOException;
import android.widget.Toast;
import android.app.ProgressDialog;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.database.SQLException;

import android.content.Intent;
import android.content.res.Resources;

import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.utils.Util;
import com.lysdev.transperthcached.timetable.DatabaseHelper;


public class MainActivity extends TabActivity
                       implements TabHost.OnTabChangeListener {

    public void tab(Intent intent, String tag, int resource_id) {
        getTabHost().addTab(
            getTabHost()
            .newTabSpec(tag)
            .setIndicator("", getResources().getDrawable(resource_id))
            .setContent(intent)
        );
    }

    public void tab(Class<?> cls, String tag, int resource_id) {
        tab(new Intent(this, cls), tag, resource_id);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = getTabHost();

        tabHost.setOnTabChangedListener(this);

        tab(TrainActivity.class,          "Train",          R.drawable.icon_train_config);
        tab(FavouriteStopsActivity.class, "FavouriteStops", R.drawable.icon_star_config);

        Intent intent = new Intent(this, StopTimetableActivity.class);
        int stop_num = getIntent().getIntExtra("stop_num", 0);
        if (stop_num != 0) {
            Log.d("TransperthCached", "Got bundled stop_num: " + stop_num);
            intent.putExtra("stop_num", stop_num);
        }
        tab(intent, "StopTimetable", R.drawable.icon_timetable_config);

        if (stop_num == 0) tabHost.setCurrentTab(0);
        else                  tabHost.setCurrentTab(2);

        final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Loading...");
                mDialog.setCancelable(false);
                mDialog.show();

        new Thread() {
            public void run() {
                try {
                    MainActivity.this.
                    initializeDB();
                } catch (java.lang.Error e) {
                    db_error(mDialog, e);
                } catch (java.io.IOException e) {
                    db_error(mDialog, e);
                }

                mDialog.dismiss();
                Log.d("TransperthCached", "initialized");
            }
        }.start();
    }

    private void db_error(ProgressDialog mDialog, Exception e) {
        Log.e("TransperthCached", "Couldn't initialize database", e);
        mDialog.dismiss();
        Toast.makeText(
            MainActivity.this,
            "Could not initialize database",
            Toast.LENGTH_LONG
        ).show();
    }

    public void initializeDB() throws IOException {
        MainActivity.instance = new DatabaseHelper(this);

        MainActivity.instance.createDataBase();

        try {
            MainActivity.instance.openDataBase();
        } catch (SQLException sqle) {
            Log.d("TransperthCached", "Caught SQLException");
            throw sqle;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MainActivity.instance != null)
            MainActivity.instance.close();
    }

    public void onTabChanged(String tabID) {
        Util.hideSoftKeyboard(this);
    }

    private static DatabaseHelper instance;
    public static DatabaseHelper getConstantDB() {
        return MainActivity.instance;
    }
}
