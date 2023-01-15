package com.gakk.noorlibrary.util

import com.gakk.noorlibrary.extralib.azan.Azan
import com.gakk.noorlibrary.extralib.azan.AzanTimes
import com.gakk.noorlibrary.extralib.azan.Method
import com.gakk.noorlibrary.extralib.azan.astrologicalCalc.Location
import com.gakk.noorlibrary.extralib.azan.astrologicalCalc.SimpleDate
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun getTodayAzanTimes(lat: Double, lang: Double): AzanTimes {
    val cal = GregorianCalendar()
    val today = SimpleDate(cal)
    val location = Location(lat, lang, getGMTOffSet(cal), 0)
    val azan = Azan(location, Method.KARACHI_HANAF)
    val prayerTimes = azan.getPrayerTimes(today)
    return prayerTimes
}

fun getAzanTimesByDate(lat: Double, lng: Double, date: Date): AzanTimes {
    val cal = GregorianCalendar()
    cal.time = date
    val simpleDate = SimpleDate(cal)
    val location = Location(lat, lng, getGMTOffSet(cal), 0)
    val azan = Azan(location, Method.KARACHI_HANAF)

    return azan.getPrayerTimes(simpleDate)
}

fun getTomorrowAzanTimes(lat: Double, lang: Double): AzanTimes {

    var cal = GregorianCalendar()
    cal.add(Calendar.DATE, 1)
    val tomorrow = SimpleDate(cal)
    val location = Location(lat, lang, getGMTOffSet(cal), 0)
    val azan = Azan(location, Method.KARACHI_HANAF)

    val prayerTimes = azan.getPrayerTimes(tomorrow)
    return prayerTimes

}

fun getGMTOffSet(cal: Calendar): Double {
    try {
        val date: DateFormat = SimpleDateFormat("z", Locale.US)
        val gmt: String = date.format(cal.time)
        return gmt.replace(Regex("""[+,-,:00,:30,GMT]"""), "").toDouble()
    } catch (e: Exception) {
        return 2.00;
    }

}

