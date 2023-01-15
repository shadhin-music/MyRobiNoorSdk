package com.gakk.noorlibrary.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(mContext: Context) {
    var context: Context
    var mFusedClient: FusedLocationProviderClient? = null

    init {
        context = mContext
        mFusedClient = LocationServices.getFusedLocationProviderClient(mContext)
    }

    fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        mFusedClient?.lastLocation?.addOnSuccessListener { it: Location? ->
            if (it != null) {
                val mLocation = com.gakk.noorlibrary.model.UserLocation(it.latitude, it.longitude)
                AppPreference.saveUserCurrentLocation(mLocation)
                Log.e("Location", "Found${it.latitude} ${it.longitude}")
            } else {
                Log.e("Location", "Not Found")
            }
        }

    }
}