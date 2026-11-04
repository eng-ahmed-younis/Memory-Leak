package com.example.memoryleak.leaks.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class LocationListenerLeak(private val context: Context) {
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    fun triggerLeak(): String {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val listener = LocationListener { location: Location -> }
        locationListener = listener
        // LEAK: Location updates requested using Activity context without removeUpdates() call.
        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, listener)
        } catch (e: Exception) {
            // Simulated permission / framework call
        }
        return "Location updates registered with LocationManager without calling removeUpdates()."
    }

    fun triggerFix(): String {
        locationListener?.let {
            try {
                locationManager?.removeUpdates(it)
            } catch (e: Exception) {
                // Ignore
            }
            locationListener = null
        }
        return "Fixed! Removed LocationListener updates from LocationManager."
    }
}
