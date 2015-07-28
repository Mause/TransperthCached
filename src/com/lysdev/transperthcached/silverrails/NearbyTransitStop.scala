package com.lysdev.transperthcached.silverrails;


public class NearbyTransitStop extends TransitStop {
    public NearbyTransitStop(
            int distance,

            double latitude, double longitude,
            String description,
            int code
            ) {
        super(latitude, longitude, description, code);
        this.distance = distance;
    }

    public String toString() {
        return String.format(
            "%d metres to %s",
            getDistance().intValue(),
            getDescription()
        );
    }


    private Integer distance;

    public Integer getDistance() {
        return this.distance;
    }
}
