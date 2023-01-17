package com.gakk.noorlibrary.model.roza

import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.extralib.azan.Time
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.in12HrFormat


data class IfterAndSehriTime(
    val dateMs: Long,
    val sehriTIme: Time,
    val ifterTime: Time
) {


    override fun toString(): String {
        return "DayOfMon:${CalenderUtil.getGrgDayOfCurrentMonth(dateMs)} S:$sehriTIme I:$ifterTime"
    }
    /**
     * Formatted & Localised Sehri time String
     */
    var sehriTimeStr: String
        get() = "${
            TimeFormtter.getNumberByLocale(sehriTIme.hour.in12HrFormat().toString())!!}:${
            TimeFormtter.getNumberByLocale(
                sehriTIme.minute.toString()
            )!!
        }"
        set(value) {
            sehriTimeStr = value
        }

    /**
     * Formatted & Localised Ifter time String
     */
    var ifterTimeStr: String
        get() = "${TimeFormtter.getNumberByLocale(ifterTime.hour.in12HrFormat().toString())!!}:${
            TimeFormtter.getNumberByLocale(
                ifterTime.minute.toString()
            )!!
        }"
        set(value) {
            ifterTimeStr = value
        }

    /**
     * returns true when this contents day of month matches todays day of month
     * false otherwise
     */
    var isToday: Boolean
        get() {
            var today = CalenderUtil.getGrgDayOfCurrentMonth(System.currentTimeMillis())
            var dateMsDay = CalenderUtil.getGrgDayOfCurrentMonth(dateMs)
            return today == dateMsDay
        }
        set(value) {
            isToday = value
        }

    /**
     * returns true when this contents day of month matches tomorrows day of month
     * false otherwise
     */
    var isTomorrow: Boolean
        get() {
            var tomorrow = CalenderUtil.getGrgTomorrow(System.currentTimeMillis())
            var dateMsDay = CalenderUtil.getGrgDayOfCurrentMonth(dateMs)
            return tomorrow == dateMsDay
        }
        set(value) {
            isTomorrow = value
        }

    var dayOfGeorgianMonth: String
        get() = TimeFormtter.getNumberByLocale(
            CalenderUtil.getGrgDayOfCurrentMonth(dateMs).toString()
        )!!
        set(value) {

            dayOfGeorgianMonth = value
        }

    var dayOfHizriMonth: String
        get() = TimeFormtter.getNumberByLocale(
            CalenderUtil.getHzrDayOfCurrentMonth(dateMs).toString()
        )!!
        set(value) {
            dayOfGeorgianMonth = value
        }

    var dayOfWeek: String
        get() {
            val index = CalenderUtil.getDayOfWeek(dateMs) - 1
            val _daysOfWeek: List<String> =
                Noor.appContext?.resources?.getStringArray(R.array.week_name)!!.toList()
            return _daysOfWeek.get(index)
        }
        set(value) {

            dayOfWeek = value
        }
}


