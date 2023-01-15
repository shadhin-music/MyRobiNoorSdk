package com.gakk.noorlibrary.util

import android.content.Context
import android.util.Log
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.extralib.azan.AzanTimes
import com.gakk.noorlibrary.model.UpCommingPrayer
import com.gakk.noorlibrary.model.UserLocation
import java.util.*

class PrayerTimeCalculator(mContext: Context) {

    private val context: Context
    var userLastKnownLocation: UserLocation
    var prayerTimesToday: AzanTimeFormatted
    var prayerTimesTomorrow: AzanTimeFormatted
    var allWaqtOverForTOday: Boolean

    init {
        allWaqtOverForTOday = false
        context = mContext
        userLastKnownLocation = AppPreference.getUserCurrentLocation()
        prayerTimesToday = AzanTimeFormatted(
            getTodayAzanTimes(
                userLastKnownLocation.lat!!,
                userLastKnownLocation.lng!!
            )
        )
        prayerTimesTomorrow = AzanTimeFormatted(
            getTomorrowAzanTimes(
                userLastKnownLocation.lat!!,
                userLastKnownLocation.lng!!
            )
        )
    }

    fun getPrayerTimeByDate(date: Date): AzanTimeFormatted {

        return AzanTimeFormatted(
            getAzanTimesByDate(
                userLastKnownLocation.lat!!,
                userLastKnownLocation.lng!!, date
            )
        )
    }

    fun getPrayerTime24ByDate(date: Date): AzanTimes {

        return getAzanTimesByDate(
            userLastKnownLocation.lat!!,
            userLastKnownLocation.lng!!, date
        )
    }


    @JvmName("getPrayerTimesToday1")
    fun getPrayerTimesToday(): AzanTimeFormatted {
        return prayerTimesToday
    }

    fun getUpCommingMalayPrayer(): UpCommingPrayer {
        allWaqtOverForTOday = false

        val upCommingPrayer = UpCommingPrayer()
        var curMillis = Date().time
        val temp = TimeFormtter.getDateStringFromMilliSecondsIn12HrFormat(curMillis)
        curMillis = TimeFormtter.milliSecondsFromTimeString(temp)

        var fajrMillisToday: Long = 0
        var dhuhrMillisToday: Long = 0
        var asrMillisToday: Long = 0
        var magribMillisToday: Long = 0
        var ishaMillisToday: Long = 0
        var fajrMillisTomorrow: Long = 0

        val namazTime = AppPreference.loadAllMalayNamazTime("All")
        Log.e("PrayerTimeCalculator", ": ${namazTime?.size}");
        namazTime?.let { arrayList ->
            val cal = Calendar.getInstance()
            cal.time = Date()
            val dayofmonth = cal.get(Calendar.DAY_OF_MONTH) - 1
            val dayOfNextMonth = cal.get(Calendar.DAY_OF_MONTH)
            if (arrayList.isNotEmpty() && arrayList.size>dayofmonth) {
                val dailyPrayerTime = arrayList.get(dayofmonth)
                if (dailyPrayerTime.size > 5) {
                    val fajrMalay = dailyPrayerTime[0]
                    val johurMalay = dailyPrayerTime[2]
                    val asrMalay = dailyPrayerTime[3]
                    val magribMalay = dailyPrayerTime[4]
                    val eshaMalay = dailyPrayerTime[5]


                    var nextFajrIndex = 0L
                    if (dayOfNextMonth < arrayList.size) {
                        nextFajrIndex = arrayList.get(dayOfNextMonth)[0]
                    }
                    fajrMillisToday = TimeFormtter.milliSecondsFromTimeStamp(fajrMalay)
                    dhuhrMillisToday = TimeFormtter.milliSecondsFromTimeStamp(
                        johurMalay
                    )
                    asrMillisToday = TimeFormtter.milliSecondsFromTimeStamp(
                        asrMalay
                    )
                    magribMillisToday = TimeFormtter.milliSecondsFromTimeStamp(
                        magribMalay
                    )
                    ishaMillisToday = TimeFormtter.milliSecondsFromTimeStamp(
                        eshaMalay
                    )

                    fajrMillisTomorrow = TimeFormtter.milliSecondsFromTimeStamp(
                        nextFajrIndex
                    )

                    when {
                        curMillis < fajrMillisToday -> {
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    ishaMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    fajrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_fajr)
                            upCommingPrayer.nextWaqtNameTracker =
                                context.getString(R.string.txt_fajr)
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_esha)
                        }
                        curMillis < dhuhrMillisToday -> {
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    fajrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    dhuhrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_johr)
                            upCommingPrayer.nextWaqtNameTracker =
                                context.getString(R.string.txt_johr)
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_fajr)
                        }
                        curMillis < asrMillisToday -> {
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    dhuhrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    asrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_asr)
                            upCommingPrayer.nextWaqtNameTracker =
                                context.getString(R.string.txt_asr)
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_johr)
                        }
                        curMillis < magribMillisToday -> {
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    asrMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    magribMillisToday
                                )!!
                            upCommingPrayer.nextWaqtName =
                                context.getString(R.string.txt_magrib)
                            upCommingPrayer.nextWaqtNameTracker =
                                context.getString(R.string.txt_magrib)
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_asr)
                        }
                        curMillis < ishaMillisToday -> {
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    magribMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    ishaMillisToday
                                )!!
                            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_esha)
                            upCommingPrayer.nextWaqtNameTracker =
                                context.getString(R.string.txt_esha)
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_magrib)
                        }
                        else -> {
                            allWaqtOverForTOday = true
                            upCommingPrayer.currentWaqtStartingTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    ishaMillisToday
                                )!!
                            upCommingPrayer.nextWaqtTime =
                                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(
                                    fajrMillisTomorrow
                                )!!
                            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_fajr)
                            upCommingPrayer.nextWaqtNameTracker = " "
                            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_esha)
                        }
                    }

                    var timeLeftinMillis = when (allWaqtOverForTOday) {
                        true -> TimeFormtter.milliSecondsFromTimeStringV4(upCommingPrayer.nextWaqtTime) - (System.currentTimeMillis())
                        false -> TimeFormtter.milliSecondsFromTimeStringV3(upCommingPrayer.nextWaqtTime) - (System.currentTimeMillis())
                    }
                    //  Log.i("ASDDA","${TimeFormtter.milliSecondsFromTimeStringV4(upCommingPrayer.nextWaqtTime)} ${upCommingPrayer.nextWaqtTime}")
                    if (timeLeftinMillis < 0) {

                        timeLeftinMillis =
                            TimeFormtter.milliSecondsFromTimeStringV2(upCommingPrayer.nextWaqtTime)
                        timeLeftinMillis + -TWENTY_FOUR_HOURS_IN_MS
                        timeLeftinMillis -= curMillis

                    }

                    upCommingPrayer.timeLeft =
                        TimeFormtter.getHHMMSSFormattedString(timeLeftinMillis)
                }
            }
        }

        return upCommingPrayer
    }

    fun getUpCommingPrayer(): UpCommingPrayer {

        allWaqtOverForTOday = false

        val upCommingPrayer = UpCommingPrayer()
        var curMillis = Date().time
        val temp = TimeFormtter.getDateStringFromMilliSecondsIn12HrFormat(curMillis)
        curMillis = TimeFormtter.milliSecondsFromTimeString(temp)

        var fajrMillisToday: Long = 0
        var dhuhrMillisToday: Long = 0
        var asrMillisToday: Long = 0
        var magribMillisToday: Long = 0
        var ishaMillisToday: Long = 0
        var fajrMillisTomorrow: Long = 0

        fajrMillisToday = TimeFormtter.milliSecondsFromTimeString(prayerTimesToday.getFajr())
        dhuhrMillisToday = TimeFormtter.milliSecondsFromTimeString(
            TimeFormtter.getFormattedTime(
                prayerTimesToday.getDuhr()
            )!!
        )
        asrMillisToday = TimeFormtter.milliSecondsFromTimeString(
            prayerTimesToday.getAsr()
        )
        magribMillisToday = TimeFormtter.milliSecondsFromTimeString(
            prayerTimesToday.getMagrib()
        )
        ishaMillisToday = TimeFormtter.milliSecondsFromTimeString(
            prayerTimesToday.getIsha()
        )
        fajrMillisTomorrow = TimeFormtter.milliSecondsFromTimeString(
            prayerTimesTomorrow.getFajr()
        )

        if (curMillis < fajrMillisToday) {

            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(ishaMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(fajrMillisToday)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_fajr)
            upCommingPrayer.nextWaqtNameTracker = context.getString(R.string.txt_fajr)
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_esha)
        } else if (curMillis < dhuhrMillisToday) {
            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(fajrMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(dhuhrMillisToday)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_johr)
            upCommingPrayer.nextWaqtNameTracker = context.getString(R.string.txt_johr)
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_fajr)
        } else if (curMillis < asrMillisToday) {

            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(dhuhrMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(asrMillisToday)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_asr)
            upCommingPrayer.nextWaqtNameTracker = context.getString(R.string.txt_asr)
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_johr)
        } else if (curMillis < magribMillisToday) {

            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(asrMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(magribMillisToday)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_magrib)
            upCommingPrayer.nextWaqtNameTracker = context.getString(R.string.txt_magrib)
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_asr)
        } else if (curMillis < ishaMillisToday) {

            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(magribMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(ishaMillisToday)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_esha)
            upCommingPrayer.nextWaqtNameTracker = context.getString(R.string.txt_esha)
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_magrib)
        } else {

            allWaqtOverForTOday = true
            upCommingPrayer.currentWaqtStartingTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(ishaMillisToday)!!
            upCommingPrayer.nextWaqtTime =
                TimeFormtter.getDateStringFromMilliSecondsIn12HrFormatV2(fajrMillisTomorrow)!!
            upCommingPrayer.nextWaqtName = context.getString(R.string.txt_fajr)
            upCommingPrayer.nextWaqtNameTracker = " "
            upCommingPrayer.currentWaqtName = context.getString(R.string.txt_esha)
        }

        var timeLeftinMillis = when (allWaqtOverForTOday) {
            true -> TimeFormtter.milliSecondsFromTimeStringV4(upCommingPrayer.nextWaqtTime) - (System.currentTimeMillis())
            false -> TimeFormtter.milliSecondsFromTimeStringV3(upCommingPrayer.nextWaqtTime) - (System.currentTimeMillis())
        }
        //  Log.i("ASDDA","${TimeFormtter.milliSecondsFromTimeStringV4(upCommingPrayer.nextWaqtTime)} ${upCommingPrayer.nextWaqtTime}")
        if (timeLeftinMillis < 0) {

            timeLeftinMillis =
                TimeFormtter.milliSecondsFromTimeStringV2(upCommingPrayer.nextWaqtTime)
            timeLeftinMillis + -TWENTY_FOUR_HOURS_IN_MS
            timeLeftinMillis -= curMillis

        }

        upCommingPrayer.timeLeft = TimeFormtter.getHHMMSSFormattedString(timeLeftinMillis)

        //Log.e("timeleftmilli","check "+upCommingPrayer.timeLeft)
        return upCommingPrayer
    }
}