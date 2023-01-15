package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.UserLocation
import com.gakk.noorlibrary.util.TAG_ALARM

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
/**
 * This Receiver class is used to listen for Broadcast Intents that announce
 * that a location change has occurred while this application isn't visible.
 *
 * Where possible, this is triggered by a Passive Location listener.
 */
class PassiveLocationChangedReceiver : BroadcastReceiver() {
    var alarm = SalaatAlarmReceiver()

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG_ALARM, "passive location called")
        val key = LocationManager.KEY_LOCATION_CHANGED
        val location: Location?

        if (intent!!.hasExtra(key)) {
            // This update came from Passive provider, so we can extract the location
            // directly.
            location = intent.extras!![key] as Location?
            if (location != null) {

                AppPreference.saveUserCurrentLocation(
                    location = UserLocation(
                        location.latitude,
                        location.longitude
                    )
                )
                alarm.cancelAlarm(context)
                alarm.setAlarm(context!!)

            }
        }
    }
}