package com.gakk.noorlibrary.ui.fragments.azan.azan_schedular

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.extralib.azan.AzanTimes
import com.gakk.noorlibrary.util.*
import java.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/4/2021, Tue
 */
class SalaatAlarmReceiver : BroadcastReceiver() {
    // The app's AlarmManager, which provides access to the system alarm services.
    private var alarmMgr: AlarmManager? = null

    // The pending intent that is triggered when the alarm fires.
    private var alarmIntent: PendingIntent? = null
    private var context: Context? = null

    var FIVE_MINUTES: Long = 60000 * 5

    var ALARM_ID = 1010
    var PASSIVE_LOCATION_ID = 1011


    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG_ALARM, "salat alarm receiver called")
        this.context = context

        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME)
        val prayerTime = intent.getLongExtra(EXTRA_PRAYER_TIME, -1)

        val timePassed =
            prayerTime != -1L && Math.abs(System.currentTimeMillis() - prayerTime) > FIVE_MINUTES
        Log.d(TAG_ALARM, "Alarm timePassed Got $timePassed")


        if (!timePassed) {
            val service = Intent(context, SalaatSchedulingService::class.java)
            service.putExtra(EXTRA_PRAYER_NAME, prayerName)

            context.startService(service)
            Log.d("alarm_debug", "onReceive: startForegroundService ")
        }
        //SET THE NEXT ALARM
        setAlarm(context)

    }

    fun getMalayNamazTimes(): ArrayList<String> {
        val prayerTimes: List<List<Long>>? = AppPreference.loadAllMalayNamazTime("All")
        val namazTimesMalay = ArrayList<String>()
        prayerTimes?.let { arrayList ->
            val cal = Calendar.getInstance()
            cal.time = Date()
            val dayofmonth = cal.get(Calendar.DAY_OF_MONTH) - 1
            if (arrayList.isNotEmpty() && arrayList.size > dayofmonth) {
                val dailyPrayerTime = arrayList.get(dayofmonth)
                if (dailyPrayerTime.size > 5) {
                    val fajrMalay = dailyPrayerTime[0]
                    val johurMalay = dailyPrayerTime[2]
                    val asrMalay = dailyPrayerTime[3]
                    val magribMalay = dailyPrayerTime[4]
                    val eshaMalay = dailyPrayerTime[5]

                    TimeFormtter.getTime(fajrMalay)?.let {
                        namazTimesMalay.add(it)
                    }
                    TimeFormtter.getTime(johurMalay)
                        ?.let {
                            namazTimesMalay.add(it)
                        }
                    TimeFormtter.getTime(asrMalay)
                        ?.let {
                            namazTimesMalay.add(it)
                        }
                    TimeFormtter.getTime(magribMalay)
                        ?.let {
                            namazTimesMalay.add(it)
                        }
                    TimeFormtter.getTime(eshaMalay)
                        ?.let {
                            namazTimesMalay.add(it)
                        }

                }
            }
        }

        return namazTimesMalay
    }

    fun setAlarm(context: Context) {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SalaatAlarmReceiver::class.java)


        val now = Calendar.getInstance(TimeZone.getDefault())
        now.timeInMillis = System.currentTimeMillis()

        // Set the alarm's trigger time to 8:30 a.m.

        var then = Calendar.getInstance(TimeZone.getDefault())
        then.timeInMillis = System.currentTimeMillis()

        val date = Date()

        val namazTime = getMalayNamazTimes()
        var prayerTime: AzanTimes? = null
        if (namazTime.isNullOrEmpty()) {
            val prayerTimeCalculator = PrayerTimeCalculator(context)
            prayerTime = prayerTimeCalculator.getPrayerTime24ByDate(date)
        }

        val array = arrayOf(
            AppPreference.IS_FAJR_ALARM_SET,
            AppPreference.IS_DHUHR_ALARM_SET,
            AppPreference.IS_ASR_ALARM_SET,
            AppPreference.IS_MAGHRIB_ALARM_SET,
            AppPreference.IS_ISHA_ALARM_SET
        )
        var nextAlarmFound = false
        var nameOfPrayerFound = 0

        var time: String

        for (i in 0..4) {
            if (!isAlarmEnabledForPrayer(array[i])) {
                continue
            }
            time = getNextTime(i, prayerTime, namazTime)
            then = getCalendarFromPrayerTime(then, time)

            if (then.after(now)) {
                // this is the alarm to set
                nameOfPrayerFound = i
                nextAlarmFound = true
                break
            }

        }

        if (!nextAlarmFound) {
            for (i in 0..4) {
                if (!isAlarmEnabledForPrayer(array[i])) {
                    continue
                }

                time = getNextTime(i, prayerTime, namazTime)
                then = getCalendarFromPrayerTime(then, time)

                if (then.before(now)) {
                    // this is the next day.
                    nameOfPrayerFound = i
                    nextAlarmFound = true
                    then.add(Calendar.DAY_OF_YEAR, 1)
                    break
                }
            }
        }

        if (!nextAlarmFound) {
            return
        }

        val name =
            getPrayerNameFromIndex(context, nameOfPrayerFound)
        Log.e("SABR", "setAlarm: intent ${then.timeInMillis}")
        intent.putExtra(EXTRA_PRAYER_NAME, name)
        intent.putExtra(EXTRA_PRAYER_TIME, then.timeInMillis)

        alarmIntent =
            PendingIntent.getBroadcast(
                context, ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT
                        or PendingIntent.FLAG_IMMUTABLE
            )

        if (SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            //lollipop_mr1 is 22, this is only 23 and above
            if (SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmMgr!!.canScheduleExactAlarms()) {
                    Log.e("Noor", "canScheduleExactAlarms")
                    alarmMgr!!.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        then.timeInMillis,
                        alarmIntent
                    )
                } else {
                    Log.e("Noor", "can not ScheduleExactAlarms")
                }
            } else {
                alarmMgr!!.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    then.timeInMillis,
                    alarmIntent
                )
            }

        } else {
            //JB_MR2 is 18, this is only 19 and above.
            alarmMgr!!.setExact(AlarmManager.RTC_WAKEUP, then.timeInMillis, alarmIntent)
        }


        val flags =
            if (SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        // SET PASSIVE LOCATION RECEIVER
        val passiveIntent = Intent(context, PassiveLocationChangedReceiver::class.java)
        val locationListenerPassivePendingIntent = PendingIntent.getActivity(
            context,
            PASSIVE_LOCATION_ID,
            passiveIntent,
            flags
        )

        requestPassiveLocationUpdates(context, locationListenerPassivePendingIntent)

        // device is rebooted.

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        val receiver = ComponentName(context, SalaatBootReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

    }

    // END_INCLUDE(cancel_alarm)
    private fun requestPassiveLocationUpdates(context: Context, pendingIntent: PendingIntent?) {
        val oneHourInMillis = (1000 * 60 * 60).toLong()
        val fiftyKinMeters: Long = 50000
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                oneHourInMillis, fiftyKinMeters.toFloat(), pendingIntent!!
            )
        } catch (se: SecurityException) {
            Log.d(TAG_ALARM, se.message, se)
            //do nothing. We should always have permision in order to reach this screen.
        }
    }

    fun cancelAlarm(context: Context?) {
        context?.let {
            if (alarmMgr == null) {
                alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            }
        }
        alarmMgr?.let {
            if (alarmIntent == null) {
                val intent = Intent(context, SalaatAlarmReceiver::class.java)
                alarmIntent = PendingIntent.getBroadcast(
                    context,
                    ALARM_ID,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            it.cancel(alarmIntent)


            //REMOVE PASSIVE LOCATION RECEIVER
            val passiveIntent = Intent(context, PassiveLocationChangedReceiver::class.java)
            val locationListenerPassivePendingIntent = PendingIntent.getActivity(
                context,
                PASSIVE_LOCATION_ID,
                passiveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            removePassiveLocationUpdates(context, locationListenerPassivePendingIntent)
        }

        context?.let {
            // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
            // alarm when the device is rebooted.
            val receiver = ComponentName(it, SalaatBootReceiver::class.java)
            val pm = it.packageManager
            pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

    }
}

private fun getNextTime(i: Int, prayerTime: AzanTimes?, namazTime: ArrayList<String>): String {

    prayerTime?.let {
        return when (i) {
            0 -> {
                prayerTime.fajr().toString()
            }
            1 -> {
                prayerTime.thuhr().toString()
            }
            2 -> {
                prayerTime.assr().toString()
            }
            3 -> {
                prayerTime.maghrib().toString()
            }
            4 -> {
                prayerTime.ishaa().toString()
            }
            else -> prayerTime.ishaa().toString()
        }
    } ?: run {
        return when (i) {
            0, 1, 2, 3, 4 -> namazTime[i]
            else -> namazTime[4]
        }
    }
}


private fun getPrayerNameFromIndex(context: Context, prayerIndex: Int): String? {
    var prayerName: String? = null
    when (prayerIndex) {
        0 -> prayerName = context.getString(R.string.fajr)
        1 -> prayerName = context.getString(R.string.dhuhr)
        2 -> prayerName = context.getString(R.string.asr)
        3 -> prayerName = context.getString(R.string.maghrib)
        4 -> prayerName = context.getString(R.string.isha)
    }
    return prayerName
}


private fun removePassiveLocationUpdates(
    context: Context?,
    pendingIntent: PendingIntent
) {
    val locationManager =
        context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    try {
        locationManager.removeUpdates(pendingIntent)
    } catch (se: SecurityException) {
        //do nothing. We should always have permision in order to reach this screen.
    }
}

private fun isAlarmEnabledForPrayer(s: String): Boolean {
    return AppPreference.getAlarmForAzan(s)
}

private fun getCalendarFromPrayerTime(cal: Calendar, prayerTime: String): Calendar {
    val time = prayerTime.split(":".toRegex()).toTypedArray()
    Log.d(TAG_ALARM, "getCalendarFromPrayerTime: ${time[0]} ${time[1]}")
    cal[Calendar.HOUR_OF_DAY] = Integer.valueOf(time[0])
    cal[Calendar.MINUTE] = Integer.valueOf(time[1])
    cal[Calendar.SECOND] = 0
    cal[Calendar.MILLISECOND] = 0
    return cal
}
