package com.lysdev.transperthcached.silverrails

class NearbyTransitStop(
            distance: Int,

            latitude: Double, longitude: Double,
            description: String,
            code: Int
            ) extends TransitStop(latitude, longitude, description, code) {

    override
    def toString() = f"$getDistance.intValue metres to $getDescription"
    def getDistance() = this.distance
}
