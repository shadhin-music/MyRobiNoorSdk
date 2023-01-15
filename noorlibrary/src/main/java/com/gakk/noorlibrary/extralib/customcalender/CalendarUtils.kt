@file:JvmName("CalendarUtils")

package com.gakk.noorlibrary.extralib.customcalender

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Applandeo Team.
 */

/**
 *Utils method to create drawable containing text
 */


/**
 * This method returns a list of calendar objects between two dates
 * @param this representing a first selected date
 * @param toCalendar Calendar representing a last selected date
 * @return List of selected dates between two dates
 */
fun Calendar.getDatesRange(toCalendar: Calendar): List<Calendar> =
        if (toCalendar.before(this)) {
            getCalendarsBetweenDates(toCalendar.time, this.time)
        } else {
            getCalendarsBetweenDates(this.time, toCalendar.time)
        }

private fun getCalendarsBetweenDates(dateFrom: Date, dateTo: Date): List<Calendar> {
    val calendars = mutableListOf<Calendar>()

    val calendarFrom = Calendar.getInstance().apply { time = dateFrom }
    val calendarTo = Calendar.getInstance().apply { time = dateTo }

    val daysBetweenDates = TimeUnit.MILLISECONDS.toDays(
            calendarTo.timeInMillis - calendarFrom.timeInMillis)

    (1 until daysBetweenDates).forEach {
        val calendar = calendarFrom.clone() as Calendar
        calendars.add(calendar)
        calendar.add(Calendar.DATE, it.toInt())
    }
    return calendars
}
