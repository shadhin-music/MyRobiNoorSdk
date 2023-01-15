package com.gakk.noorlibrary.model.calender

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */

data class IslamicCalendarModel(
    private val str: String,
) {
    var dayTxt: String = str
    var isToday: Boolean = false
}
