package com.lysdev.transperthcached.models;

import com.lysdev.transperthcached.activities.MainActivity;

public class FavouriteStop {
    private String stop_number;
    private Integer sid = null;
    private String description = null;

    public FavouriteStop(String stop_number) {
        this.stop_number = stop_number;
    }

    public FavouriteStop(Integer sid, String stop_number) {
        this(stop_number);
        this.sid = sid;
    }

    public String getStopNumber() {
        return this.stop_number;
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = MainActivity.getConstantDB().getDescriptionOfStop(
                this.stop_number
            );
        }

        return this.description;
    }
}
