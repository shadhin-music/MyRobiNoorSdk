package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gakk.noorlibrary.util.TAG_ALARM

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the alarm, the receiver is enabled.
 * When the user cancels the alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
class SalaatBootReceiver : BroadcastReceiver() {
    var salaatAlarm = SalaatAlarmReceiver()
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Log.d(TAG_ALARM, "salat BOOT receiver called")

        Log.d(TAG_ALARM, "SalaatBootReceiver $action")

        if (action == "android.intent.action.BOOT_COMPLETED") {
            salaatAlarm.setAlarm(context!!)

        } else if (action == "android.intent.action.TIMEZONE_CHANGED" || action == "android.intent.action.TIME_SET" || action == "android.intent.action.co.ibadat.gakk.azan_scheduler") {
            // Our location could have changed, which means time calculations may be different
            // now so cancel the alarm and set it again.
            salaatAlarm.cancelAlarm(context)
            salaatAlarm.setAlarm(context!!)

        }
    }
}