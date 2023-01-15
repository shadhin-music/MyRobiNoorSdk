package com.gakk.noorlibrary.ui.fragments.calender

import android.content.Context
import android.util.Log
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import com.gakk.noorlibrary.model.calender.IslamicCalendarModel
import com.gakk.noorlibrary.util.CalendarUtility
import com.gakk.noorlibrary.util.LAN_BANGLA
import com.gakk.noorlibrary.util.TimeFormtter


import java.util.*
import kotlin.collections.ArrayList



object CustomIslamicCalendarDataBd {
    fun getIslamicMonthData(i: Int): ArrayList<IslamicCalendarModel> {
        if (i < 0) {
            return arrayListOf()
        }
        val arrayList: ArrayList<IslamicCalendarModel> = ArrayList()
        val ummalquraCalendar = UmmalquraCalendar(
            /*TimeZone.getTimeZone("GMT+3"),
            Locale(
                "ar" , "SA"
            )*/
        )
        ummalquraCalendar.add(Calendar.DATE, -1)
        val todayDate =  ummalquraCalendar.time
        ummalquraCalendar.set(Calendar.MONTH, i)
        ummalquraCalendar.set(Calendar.DATE, 1)
        val dayOfWeek = ummalquraCalendar.get(Calendar.DAY_OF_WEEK)


        Log.e("datechkhijri",""+todayDate)

        //arrayList.add(IslamicCalendarModel(" "))
        if (dayOfWeek != 7) {
            for (day in 0 until dayOfWeek) {
                arrayList.add(IslamicCalendarModel(" "))
            }
        }
        for (day in 0..31) {
            ummalquraCalendar.get(Calendar.YEAR)
            if (day > 0) {
                ummalquraCalendar.add(Calendar.DATE, 1)
            }
            val currentMonth = ummalquraCalendar.get(Calendar.MONTH)
            val curDate = ummalquraCalendar.get(Calendar.DATE)
            Log.d("printVal", "getIslamicMonthData: $currentMonth $curDate")
            if (currentMonth > i) {
                break
            }
            val islamicCalendarModel = IslamicCalendarModel(curDate.toString() + "")
            islamicCalendarModel.isToday = (ummalquraCalendar.time == todayDate)
            arrayList.add(islamicCalendarModel)
        }
        return arrayList
    }

    fun getIslamicToDay(context: Context, i: Int): String {
        val ummalquraCalendar = UmmalquraCalendar()
        ummalquraCalendar[2] = i
        val calendarUtility = CalendarUtility()
        var islamicMonthName: String = calendarUtility.getIslamicMonthNameEn(
            ummalquraCalendar[2]
        )
        val string: String = LAN_BANGLA
        if (string.equals(LAN_BANGLA, ignoreCase = true)) {
            islamicMonthName = calendarUtility.getIslamicMonthName(ummalquraCalendar[2])
        }
        return " " + islamicMonthName + " " + TimeFormtter.getNumberByLocale(ummalquraCalendar[1].toString() + "")
    }

}