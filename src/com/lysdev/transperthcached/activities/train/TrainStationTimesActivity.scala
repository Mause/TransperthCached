package com.lysdev.transperthcached.activities.train;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Minutes;
import org.joda.time.DateTime;

import com.lysdev.transperthcached.livetimes.Trip;
import com.lysdev.transperthcached.livetimes.GetTimesForStation;
import com.lysdev.transperthcached.livetimes.TimesForStation;
import com.lysdev.transperthcached.livetimes.InvalidPlatformCodeException;
import com.lysdev.transperthcached.R;


class TripDisplayWrapper {
    Trip t;
    public TripDisplayWrapper(Trip t) {
        this.t = t;
    }
    public String toString() {
        return String.format(
            "%s - %d minutes - %d cars",
            this.t.getLineFull(),
            Minutes.minutesBetween(
                DateTime.now(),
                this.t.getActual()
            ).getMinutes(),
            this.t.getNumCars()
        );
    }
}


public class TrainStationTimesActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_times);

        String line_name = getIntent().getStringExtra("line_name");
        String station_name = getIntent().getStringExtra("station_name");
        Direction direction = Direction.from_val("direction", getIntent());

        display_data(line_name, station_name, direction);
    }

    public String b_to_s(boolean b) {
        return b ? "true" : "false";
    }

    public void display_data(String line_name, String station_name, Direction direction) {
        TimesForStation tfp = GetTimesForStation.getTimes(station_name);

        List<Trip> trips = tfp.getTrips();
        List<TripDisplayWrapper> filtered = new ArrayList<TripDisplayWrapper>();

        for (Trip t : trips) {
            boolean going, coming;

            Log.d(
                "TransperthCached",
                String.format(
                    "%s %s",
                    t.getDestination(),
                    direction.toString()
                )
            );

            coming = t.getDestination().equals("Perth") && direction == Direction.TO;
            going =  !t.getDestination().equals("Perth") && direction == Direction.FROM;

            Log.d("TransperthCached", String.format("going: %s", b_to_s(going)));
            Log.d("TransperthCached", String.format("coming: %s", b_to_s(coming)));

            if (going || coming) filtered.add(new TripDisplayWrapper(t));
        }

        Log.d(
            "TransperthCached",
            String.format(
                "%d before, %d now",
                trips.size(),
                filtered.size()
            )
        );

        ListView times_lv = (ListView) findViewById(R.id.times);
        ListAdapter ad = new ArrayAdapter<TripDisplayWrapper>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            filtered
        );
        times_lv.setAdapter(ad);
        // times_lv.setOnItemClickListener(this);
    }
}
