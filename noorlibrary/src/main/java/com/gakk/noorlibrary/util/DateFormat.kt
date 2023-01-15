package com.gakk.noorlibrary.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFormat {

    fun getFormattedTime(inputTime: String): String? {
        val df: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        val outputformat: DateFormat = SimpleDateFormat("hh:mm:ss aa", Locale.US)
        val date: Date?
        var output: String? = null

        try {
            date = df.parse(inputTime)
            output = outputformat.format(date)
        } catch (e: ParseException) {

        }

        return output
    }
}