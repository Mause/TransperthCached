package com.lysdev.transperthcached.activities;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.content.res.Resources;

import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.utils.Util;


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
        String stop_num = getIntent().getStringExtra("stop_num");
        if (stop_num != null) {
            Log.d("TransperthCached", "Got bundled stop_num: " + stop_num);
            intent.putExtra("stop_num", stop_num);
        }
        tab(intent, "StopTimetable", R.drawable.icon_timetable_config);

        if (stop_num == null) tabHost.setCurrentTab(0);
        else                  tabHost.setCurrentTab(2);
    }

    public void onTabChanged(String tabID) {
        Util.hideSoftKeyboard(this);
    }
}
