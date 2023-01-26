package com.gakk.noorlibrary.roza

import android.content.Context
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime

/**
 * Populates Ifter and Sehri Time
 *          &
 * Saves them on SharedPreference
 */
object IfterAndSehriTimePopulationControl {

    private lateinit var ramadanIfterNSehriTimeList:MutableList<IfterAndSehriTime>
    private lateinit var nextTenDaysIfterNSehriTimeList:MutableList<IfterAndSehriTime>

    /**
     * Populates Ifter and Sehri time for next 10 days(Including today)
     */
    fun populateIfterAndSehriTimeForNextTenDays(context: Context? = null){
        nextTenDaysIfterNSehriTimeList= mutableListOf()
        var todayInMs = System.currentTimeMillis()
        for (i in 0..9) {
            var ifterAndSehriTime =
                IftarAndSehriTimeProvider.getIfterAndSehriTimeFromGivenDateByGivenOffset(
                    todayInMs,
                    i,
                    context
                )
            nextTenDaysIfterNSehriTimeList.add(ifterAndSehriTime)

        }
    }

    /**
     * Populates Ifter and Sehri time for Current Year's Ramadan
     */
    fun populateIfterAndSehriTimeForThisRamadan(context: Context? = null){
        ramadanIfterNSehriTimeList= mutableListOf()
        var firstRamadanMs = CalenderUtil.getFirstRmdnGrgMs()
        for (i in 0..29) {
            var ifterAndSehriTime =
                IftarAndSehriTimeProvider.getIfterAndSehriTimeFromGivenDateByGivenOffset(
                    firstRamadanMs,
                    i,
                    context
                )
            ramadanIfterNSehriTimeList.add(ifterAndSehriTime)
//            var date = TimeFormtter.getddMMYYYYFormattedStringFromMS(ifterAndSehriTime.dateMs)
//            var sehri = "${ifterAndSehriTime.sehriTIme.hour}:${ifterAndSehriTime.sehriTIme.minute}"
//            var iftar = "${ifterAndSehriTime.ifterTime.hour}:${ifterAndSehriTime.ifterTime.minute}"
//            Log.i("SIT", " day:$i Date:$date sehri:$sehri iftar:$iftar")
        }
    }

    fun saveRamadanSehriIfterListToSp(){
        AppPreference.ramadanSehriIfterTimes= ramadanIfterNSehriTimeList
    }

    fun saveNextTenDaysSehriIfterListToSp(){
        AppPreference.nextTenDaysSehriIfterTimes = nextTenDaysIfterNSehriTimeList
    }

    fun populateAndSaveUpdatedIfterSehriTimes(context: Context? = null){
        populateIfterAndSehriTimeForNextTenDays(context)
        populateIfterAndSehriTimeForThisRamadan(context)
        saveRamadanSehriIfterListToSp()
        saveNextTenDaysSehriIfterListToSp()
    }

}