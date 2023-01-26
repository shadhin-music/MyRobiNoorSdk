package com.gakk.noorlibrary.roza

import android.content.Context
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.extralib.azan.Azan
import com.gakk.noorlibrary.extralib.azan.Method
import com.gakk.noorlibrary.extralib.azan.astrologicalCalc.Location
import com.gakk.noorlibrary.extralib.azan.astrologicalCalc.SimpleDate
import com.gakk.noorlibrary.model.roza.IfterAndSehriTime
import com.gakk.noorlibrary.util.getGMTOffSet
import java.util.*

object IftarAndSehriTimeProvider {

    fun getIfterAndSehriTimeFromGivenDateByGivenOffset(dateMs:Long,offSetInDays:Int, context: Context? = null):IfterAndSehriTime{

       val cal = GregorianCalendar()
        cal.timeInMillis=dateMs
        cal.add(Calendar.DATE, offSetInDays)
        val selectedDate = SimpleDate(cal)
        val location = Location(AppPreference.getUserCurrentLocation().lat!!,AppPreference.getUserCurrentLocation().lng!!, getGMTOffSet(cal), 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(selectedDate)

        return IfterAndSehriTime(
            cal.timeInMillis,
            prayerTimes.fajr(),
            prayerTimes.maghrib()
        )
    }
}