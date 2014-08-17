package com.lysdev.transperthcached.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.lysdev.transperthcached.R;

public class MainActivity extends TabActivity {
    public TabSpec tab(Class<?> cls, String tab_spec, int resource_id) {
        return getTabHost()
            .newTabSpec(tab_spec)
            .setIndicator("", getResources().getDrawable(resource_id))
            .setContent(
                new Intent().setClass(this, cls)
            )
        ;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        TabSpec tabSpecTrain = tab(
            TrainActivity.class, "Train", R.drawable.icon_train_config
        );

        TabSpec tabSpecFavouriteStops = tab(
            FavouriteStopsActivity.class, "FavouriteStops", R.drawable.icon_star_config
        );

        TabSpec tabSpecStopTimetable = tab(
            StopTimetableActivity.class, "StopTimetable", R.drawable.icon_timetable_config
        );

        // // Blackberry tab
        // Intent intentBerry = new Intent().setClass(this, BlackBerryActivity.class);
        // TabSpec tabSpecBerry = tabHost
        //   .newTabSpec("Berry")
        //   .setIndicator("", resources.getDrawable(R.drawable.icon_blackberry_config))
        //   .setContent(intentBerry);

        // // add all tabs
        tabHost.addTab(tabSpecTrain);
        tabHost.addTab(tabSpecFavouriteStops);
        tabHost.addTab(tabSpecStopTimetable);
        // tabHost.addTab(tabSpecBerry);

        //set StopTimetable tab as default (zero based)
        tabHost.setCurrentTab(0);
    }
}
