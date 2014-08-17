package com.lysdev.transperthcached;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        // Android tab
        Intent intentTrain = new Intent().setClass(this, TrainActivity.class);
        TabSpec tabSpecTrain = tabHost
          .newTabSpec("Train")
          .setIndicator("", resources.getDrawable(R.drawable.icon_train_config))
          .setContent(intentTrain);

        // FavouriteStops tab
        Intent intentFavouriteStops = new Intent().setClass(this, FavouriteStopsActivity.class);
        TabSpec tabSpecFavouriteStops = tabHost
          .newTabSpec("FavouriteStops")
          .setIndicator("", resources.getDrawable(R.drawable.icon_star_config))
          .setContent(intentFavouriteStops);

        // StopTimetable tab
        Intent intentStopTimetable = new Intent().setClass(this, StopTimetableActivity.class);
        TabSpec tabSpecStopTimetable = tabHost
          .newTabSpec("StopTimetable")
          .setIndicator("", resources.getDrawable(R.drawable.icon_timetable_config))
          .setContent(intentStopTimetable);

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
