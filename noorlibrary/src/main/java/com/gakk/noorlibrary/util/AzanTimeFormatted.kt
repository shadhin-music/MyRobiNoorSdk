package com.gakk.noorlibrary.util

import com.gakk.noorlibrary.extralib.azan.AzanTimes

class AzanTimeFormatted(val azanTimes: AzanTimes) {

    fun getFajr(): String {
       return TimeFormtter.get12HrFormattedStringFrom24HrFormattedString(azanTimes.fajr().toString())!!
    }

    fun getDuhr(): String {
        return TimeFormtter.get12HrFormattedStringFrom24HrFormattedString(azanTimes.thuhr().toString())!!
    }

    fun getAsr(): String {
        return TimeFormtter.get12HrFormattedStringFrom24HrFormattedString(azanTimes.assr().toString())!!
    }

    fun getMagrib(): String {
        return TimeFormtter.get12HrFormattedStringFrom24HrFormattedString(azanTimes.maghrib().toString())!!
    }

    fun getIsha(): String {
        return TimeFormtter.get12HrFormattedStringFrom24HrFormattedString(azanTimes.ishaa().toString())!!
    }
}