package com.gakk.noorlibrary.roza

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.receiver.RozaAlarmReceiver
import com.gakk.noorlibrary.util.ACTION_TYPE
import com.gakk.noorlibrary.util.SHOW_IFTER_NOTIFICATION

import com.gakk.noorlibrary.util.SHOW_SEHRI_NOTIFICATION
import com.gakk.noorlibrary.util.TimeFormtter
import java.util.*

class RozaAlarmControl(context: Context) {
    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null
    private var context: Context? = null
    private var intent: Intent? = null

    init {
        this.context = context
        alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarmForToday(type: String) {
        val requestCode = getAlarmRequestCode(type)
        val timeInMs = getAlarmTimeInMs(type)
        intent = Intent(this.context, RozaAlarmReceiver::class.java)
        intent?.putExtra(ACTION_TYPE, type)

        alarmIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent!!,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        try {
            alarmMgr?.cancel(alarmIntent)
        } catch (e: Exception) {

        }

        Log.e("JOBSCHEDULER", "${AppPreference.lastRozaAlarmDisMissTimeMs}")
        val diff = System.currentTimeMillis() - AppPreference.lastRozaAlarmDisMissTimeMs
        if (diff > (1000L * 60L) || diff < 0L) {
            Log.e(
                "JOBSCHEDULER",
                "type:$type at ${TimeFormtter.getddMMYYYYHHMMSSFormattedStringFromMS(timeInMs)}"
            )


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmMgr!!.canScheduleExactAlarms()) {
                        Log.e("Noor", "canScheduleExactAlarms")
                        alarmMgr!!.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timeInMs,
                            alarmIntent
                        )
                    } else {
                        Log.e("Noor", "can not ScheduleExactAlarms")
                    }
                } else {
                    alarmMgr!!.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMs,
                        alarmIntent
                    )
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmMgr!!.setExact(AlarmManager.RTC_WAKEUP, timeInMs, alarmIntent)
            } else {
                alarmMgr!!.set(AlarmManager.RTC_WAKEUP, timeInMs, alarmIntent)
            }

        } else {
            Log.e(
                "JOBSCHEDULER",
                "Alarm was just dismissed $diff ms  ago"
            )
        }


    }

    private fun getAlarmRequestCode(type: String): Int {
        when (type) {
            SHOW_SEHRI_NOTIFICATION -> return 1
            else -> return 2
        }
    }

    fun getAlarmTimeInMs(type: String): Long {

        var todaySehriIfterTimeInMs: Long = 0
        var todaySehriIfterTimeObj: IfterAndSehriTime? = null
        var tomorrowSehriIfterTimeObj: IfterAndSehriTime? = null

        var sehriIfterList: MutableList<IfterAndSehriTime>? = null
        when (CalenderUtil.isRamadanNow()) {
            true -> {
                sehriIfterList = AppPreference.ramadanSehriIfterTimes
            }
            false -> {
                sehriIfterList = AppPreference.nextTenDaysSehriIfterTimes

            }
        }
        sehriIfterList?.let {
            for (iterator in it) {
                if (iterator.isToday) {
                    todaySehriIfterTimeObj = iterator
                    //Log.e("seI", todaySehriIfterTimeObj.toString())
                    break
                }
            }
            for (iterator in it) {
                if (iterator.isTomorrow) {
                    tomorrowSehriIfterTimeObj = iterator
                    break
                }
            }
        }

        var gCal = GregorianCalendar()
        gCal.timeInMillis = System.currentTimeMillis()
        when (type) {
            SHOW_SEHRI_NOTIFICATION -> {

                todaySehriIfterTimeObj?.let {
                    gCal.set(Calendar.HOUR_OF_DAY, it.sehriTIme.hour)
                    gCal.set(Calendar.MINUTE, it.sehriTIme.minute)
                }

                //Log.i("TIMES","gCal:${TimeFormtter.getddMMYYYYHHMMSSFormattedStringFromMS(gCal.timeInMillis)} sehri:${TimeFormtter.getddMMYYYYHHMMSSFormattedStringFromMS(System.currentTimeMillis())} ${gCal.timeInMillis<System.currentTimeMillis()}")
                if (gCal.timeInMillis < System.currentTimeMillis()) {
                    tomorrowSehriIfterTimeObj?.let {
                        gCal.timeInMillis = it.dateMs
                        gCal.set(Calendar.HOUR_OF_DAY, it.sehriTIme.hour)
                        gCal.set(Calendar.MINUTE, it.sehriTIme.minute)
                    }
                }

            }
            SHOW_IFTER_NOTIFICATION -> {
                todaySehriIfterTimeObj?.let {
                    gCal.set(Calendar.HOUR_OF_DAY, it.ifterTime.hour)
                    gCal.set(Calendar.MINUTE, it.ifterTime.minute)
                }
                //Log.e("Check", " gCal "+gCal.timeInMillis+" current "+System.currentTimeMillis());
                if (gCal.timeInMillis < System.currentTimeMillis()) {
                    tomorrowSehriIfterTimeObj?.let {
                        gCal.timeInMillis = it.dateMs
                        gCal.set(Calendar.HOUR_OF_DAY, it.ifterTime.hour)
                        gCal.set(Calendar.MINUTE, it.ifterTime.minute)
                    }
                }
            }
        }

        todaySehriIfterTimeInMs = gCal.timeInMillis

        return todaySehriIfterTimeInMs
    }

}

