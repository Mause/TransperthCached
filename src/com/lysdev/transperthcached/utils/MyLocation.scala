package com.lysdev.transperthcached.utils

import java.util.Timer
import java.util.TimerTask

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle


class MyLocation {
    var timer1 : Timer = null
    var locationCallback : (Location => Unit) = null
    var gps_enabled : Boolean = false
    var network_enabled : Boolean = false

    var lm : LocationManager = null

    def getLocation(locationCallback: (Location => Unit))(implicit context: Context) : Boolean = {
        this.locationCallback = locationCallback;
        if (lm == null) {
            lm = (
                context
                .getSystemService(Context.LOCATION_SERVICE)
                .asInstanceOf[LocationManager]
            )
        }

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        //don't start listeners if no provider is enabled
        if(gps_enabled || network_enabled) {
            if (gps_enabled) {
                lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps
                )
            }

            if (network_enabled) {
                lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork
                )
            }

            this.timer1 = new Timer()
            this.timer1.schedule(new GetLastLocation(), 20000)

            true
        } else {
            false
        }
    }

    val locationListenerGps : LocationListener = new LocationListener() {
        def onLocationChanged(location: Location) {
            timer1.cancel()
            locationCallback(location)
            lm.removeUpdates(this)
            lm.removeUpdates(locationListenerNetwork)
        }
        def onProviderDisabled(provider: String) = {}
        def onProviderEnabled(provider: String) = {}
        def onStatusChanged(provider: String, status: Int, extras: Bundle) = {}
    }

    val locationListenerNetwork : LocationListener = new LocationListener() {
        def onLocationChanged(location: Location) {
            timer1.cancel()
            locationCallback(location)
            lm.removeUpdates(this)
            lm.removeUpdates(locationListenerGps)
        }
        def onProviderDisabled(provider: String) = {}
        def onProviderEnabled(provider: String) = {}
        def onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    class GetLastLocation extends TimerTask {
        override def run() {
            lm.removeUpdates(locationListenerGps)
            lm.removeUpdates(locationListenerNetwork)

            var net_loc : Location = null
            var gps_loc : Location = null
            if(gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if(network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

             //if there are both values use the latest one
            if (gps_loc != null && net_loc != null){
                if (gps_loc.getTime() > net_loc.getTime()) {
                    locationCallback(gps_loc)
                } else {
                    locationCallback(net_loc)
               }
               return
            }

            if(gps_loc != null){
                locationCallback(gps_loc)
                return
            }

            if(net_loc != null){
                locationCallback(net_loc)
                return
            }

            locationCallback(null)
        }
    }
}
