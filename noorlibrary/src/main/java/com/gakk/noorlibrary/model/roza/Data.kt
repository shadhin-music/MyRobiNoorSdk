package com.gakk.noorlibrary.model.roza

import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.roza.CalenderUtil
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.in12HrFormat

data class Data(
    val about: Any,
    val createdBy: Any,
    val createdOn: String,
    val id: Any,
    val iftar: String,
    val isActive: Boolean,
    val language: String,
    val order: Int,
    val ramadaDate: String,
    val ramadaDay: String,
    val sehri: String,
    val state: Any,
    val updatedBy: Any,
    val updatedOn: Any
){

    /**
     * Formatted & Localised Sehri time String
     */
    var sehriTimeStr1: String
        get() = "${
            TimeFormtter.getNumberByLocale(sehri)!!}"
        set(value) {
            sehriTimeStr1 = value
        }

    /**
     * Formatted & Localised Ifter time String
     */
    var ifterTimeStr1: String
        get() = "${TimeFormtter.getNumberByLocale(iftar)!!}"
        set(value) {
            ifterTimeStr1 = value
        }
    /**
     * returns true when this contents day of month matches todays day of month
     * false otherwise
     */
    var isToday: Boolean
        get() {
            var today = CalenderUtil.getGrgDayOfCurrentMonth(System.currentTimeMillis())
            var date = TimeFormtter.MillisecondFromDateString(ramadaDate)
            var dateMsDay = CalenderUtil.getGrgDayOfCurrentMonth(date!!)
            return today == dateMsDay
        }
        set(value) {
            isToday = value
        }

    var dayOfWeek: String
        get() {

            var date = TimeFormtter.MillisecondFromDateString(ramadaDate)
            val index = CalenderUtil.getDayOfWeek(date!!) - 1
            val _daysOfWeek: List<String> =
                BaseApplication.getAppContext().resources.getStringArray(R.array.week_name).toList()
            return _daysOfWeek.get(index)
        }
        set(value) {

            dayOfWeek = value
        }
}