package com.gakk.noorlibrary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.service.RozaAlarmService
import com.gakk.noorlibrary.util.ACTION_TYPE
import com.gakk.noorlibrary.util.DISMISS_ROZA_NOTIFICATION_SERVICE
import com.gakk.noorlibrary.util.SHOW_IFTER_NOTIFICATION
import com.gakk.noorlibrary.util.SHOW_SEHRI_NOTIFICATION


class RozaAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.e("JOBSCHEDULER", "Alarm Invoked")
        AppPreference.cachedUser?.let{
            val type=intent.getStringExtra(ACTION_TYPE)
            when(type){
                SHOW_SEHRI_NOTIFICATION -> {
                    Log.e("JOBSCHEDULER", "Sehri Alarm Invoked")
                    if(AppPreference.sehriAlertOn && AppPreference.nitificationflag){
                        Intent(context, RozaAlarmService::class.java).also {

                            it.putExtra(ACTION_TYPE, SHOW_SEHRI_NOTIFICATION)
                            context.startService(it)

                        }
                    }else{

                    }

                }
                SHOW_IFTER_NOTIFICATION -> {
                    Log.e("JOBSCHEDULER", "Ifter Alarm Invoked")
                    if(AppPreference.ifterAlertOn && AppPreference.nitificationflag){
                        Intent(context, RozaAlarmService::class.java).also {

                            it.putExtra(ACTION_TYPE, SHOW_IFTER_NOTIFICATION)
                            context.startService(it)
                        }
                    }else{

                    }

                    //invoke ifter alarm for today
                }
                DISMISS_ROZA_NOTIFICATION_SERVICE -> {
                    Log.i("RECEIVER", "dismiss called")

                    Intent(context, RozaAlarmService::class.java).also {
                        context.stopService(it)
                    }
                }
                else->{}
            }
        }

    }
}