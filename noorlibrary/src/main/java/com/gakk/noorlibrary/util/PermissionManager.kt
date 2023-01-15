package com.gakk.noorlibrary.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/6/2021, Tue
 */
object PermissionManager {

    fun isLocationPermissionGiven(context: Context): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermissionForLocationV2(activity: Activity, callback: (() -> Unit)? = null) {

    }


    fun requestPermissionForLocation(activity: Activity, callback: (() -> Unit)? = null) {
        /*Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted()!!) {
                        val locationHelper = LocationHelper(activity)
                        locationHelper.requestLocation()
                        callback?.invoke()
                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            activity,
                            "Location Permission Denied by user !",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).onSameThread().check()*/
    }
}