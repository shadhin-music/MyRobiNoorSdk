package com.gakk.noorlibrary.util

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeFormtter {
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

    fun get12HrFormattedStringFrom24HrFormattedString(input: String?): String? {

        //Date/time pattern of input date
        val df: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        //Date/time pattern of desired output date
        val outputformat: DateFormat = SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH)
        var date: Date? = null
        var output: String? = null
        try {
            //Conversion of input String to date
            date = df.parse(input)
            //old date format to new date format
            output = outputformat.format(date)
            println(output)
        } catch (pe: ParseException) {
            pe.printStackTrace()
        }
        return output
    }

    fun getFormattedTimeHourMinute(inputTime: String): String? {
        val df: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        val outputformat: DateFormat = SimpleDateFormat("hh:mm", Locale.US)
        val date: Date?
        var output: String? = null

        try {
            date = df.parse(inputTime)
            output = outputformat.format(date)
        } catch (e: ParseException) {

        }

        return output
    }

    fun getMsFromHHMMSS(inputTime: String): Long {
        var splittedTime = inputTime.split(":")
        var hrStr = "0"
        var minStr = "0"
        var secStr = "0"
        when (splittedTime.size) {
            3 -> {
                hrStr = splittedTime.get(0)
                minStr = splittedTime.get(1)
                secStr = splittedTime.get(2)
            }
            2 -> {
                minStr = splittedTime.get(0)
                secStr = splittedTime.get(1)
            }
        }
        return (hrStr.toLong() * 3600000) + (minStr.toLong() * 60000) + (secStr.toLong() * 1000)
    }

    fun getDateStringFromMilliSecondsIn12HrFormat(milliseconds: Long): String {
        val date = Date()
        date.time = milliseconds
        val formattedDate = SimpleDateFormat("hh:mm:ss aa", Locale.US).format(date)
        return formattedDate
    }


    fun getTimeStringFromMilliSecondsIn12HrFormatV1(milliseconds: Long): String {
        val date = Date()
        date.time = milliseconds
        val formattedDate = SimpleDateFormat("hh:mm:ss", Locale.US).format(date)
        return formattedDate
    }


    fun getFormetDateFromMiliSecond(milliseconds: Long): String {
        val date = Date()
        date.time = milliseconds
        val formattedDate = SimpleDateFormat("hh:mm aa", Locale.US).format(date)
        return formattedDate
    }

    fun getDurationFromMsByLocale(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
        if (hours > 0) {
            return "${getNumberByLocale(hours.toString())}:${getNumberByLocale(minutes.toString())}:${
                getNumberByLocale(
                    seconds.toString()
                )
            }"
        } else {
            return "${getNumberByLocale(minutes.toString())}:${getNumberByLocale(seconds.toString())}"
        }

    }

    fun milliSecondsFromTimeString(timeStr: String): Long {
        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH)
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: Exception) {
            Log.e("currmili", "chek" + e.message)
        }
        return milliSec
    }

    fun milliSecondsFromTimeStamp(timeStr: Long): Long {
        var milliSec: Long = 0
        val date = Date(timeStr * 1000L) // *1000 is to convert seconds to milliseconds
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH) // the format of your date
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")

        val outPut = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        val s = sdf.format(date)
        try {
            val fDate = outPut.parse(s)
            milliSec = fDate.time
        } catch (e: Exception) {
            Log.e("currmili", "chek" + e.message)
        }
        return milliSec
    }

    fun getTime(ts: Long): String? {
        ///"Asia/Kuala_Lumpur"
        val date = Date(ts * 1000L) // *1000 is to convert seconds to milliseconds
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH) // the format of your date
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(date)
    }

    fun milliSecondsFromTime(timeStr: String): Long {
        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm:ss", Locale.US)
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: Exception) {
            Log.e("currmili", "chek" + e.message)
        }
        return milliSec
    }

    fun getDateStringFromMilliSecondsIn12HrFormatV2(milliseconds: Long): String? {
        val date = Date()
        date.time = milliseconds
        return SimpleDateFormat("hh:mm aa", Locale.US).format(date)
    }

    fun getHHMMSSFormattedString(milliSeconds: Long): String {
        return String.format(
            Locale.ENGLISH, "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliSeconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliSeconds
                )
            )
        )
    }

    fun getMMSSFormattedString(milliSeconds: Long): String {
        return String.format(
            Locale.ENGLISH, "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliSeconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliSeconds
                )
            )
        )
    }

    fun milliSecondsFromTimeStringV2(timeStr: String?): Long {
        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm aa")
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: java.lang.Exception) {
        }
        return milliSec
    }

    fun milliSecondsFromTimeStringV3(timeStr: String?): Long {

        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: java.lang.Exception) {
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSec
        var hr = calendar[Calendar.HOUR_OF_DAY]
        var min = calendar[Calendar.MINUTE]
        var seconds = calendar[Calendar.SECOND]
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hr
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = seconds
        return calendar.timeInMillis

    }

    fun milliSecondsFromTimeStringV4(timeStr: String?): Long {

        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: java.lang.Exception) {
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSec
        var hr = calendar[Calendar.HOUR_OF_DAY]
        var min = calendar[Calendar.MINUTE]
        var seconds = calendar[Calendar.SECOND]
        calendar.timeInMillis = System.currentTimeMillis() + TWENTY_FOUR_HOURS_IN_MS
        calendar[Calendar.HOUR_OF_DAY] = hr
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = seconds
        return calendar.timeInMillis

    }

    fun milliSecondsFromTimeStringV5(timeStr: String?): Long {

        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm aa")
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            milliSec = date.time
        } catch (e: java.lang.Exception) {
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSec
        var hr = calendar[Calendar.HOUR_OF_DAY]
        var min = calendar[Calendar.MINUTE]
        var seconds = calendar[Calendar.SECOND]
        calendar.timeInMillis = System.currentTimeMillis() - TWENTY_FOUR_HOURS_IN_MS
        calendar[Calendar.HOUR_OF_DAY] = hr
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = seconds
        return calendar.timeInMillis

    }

    fun milliSecondsForTodayFomMs(milliseconds: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        var hr = calendar[Calendar.HOUR_OF_DAY]
        var min = calendar[Calendar.MINUTE]
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hr
        calendar[Calendar.MINUTE] = min
        return calendar.timeInMillis

    }

    fun milliSecondsFromTimeStringForToday(timeStr: String?): Long {
        var milliSec: Long = 0
        val formatter = SimpleDateFormat("hh:mm aa")
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hr = calendar[Calendar.HOUR_OF_DAY]
            val min = calendar[Calendar.MINUTE]
            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = hr
            calendar[Calendar.MINUTE] = min
            milliSec = calendar.timeInMillis
        } catch (e: java.lang.Exception) {
        }
        return milliSec
    }

    fun milliSecondsFromTimeStringForTodayV2(timeStr: String?): Long {
        var milliSec: Long = 0
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
        // formatter.setLenient(false);
        try {
            val date = formatter.parse(timeStr)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hr = calendar[Calendar.HOUR_OF_DAY]
            val min = calendar[Calendar.MINUTE]
            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = hr
            calendar[Calendar.MINUTE] = min
            milliSec = calendar.timeInMillis

        } catch (e: Exception) {
            Log.e("cannot", "convert")
        }
        return milliSec
    }

    fun MillisecondFromDateString(date1: String): Long? {
        val myDate = date1

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date = sdf.parse(myDate)
        val timeInMillis = date.time
        return timeInMillis
    }

    fun getddMMYYYYFormattedStringFromMS(milliseconds: Long): String? {
        val date = Date()
        date.time = milliseconds
        return SimpleDateFormat("dd-MM-yyyy").format(date)
    }

    fun getddMMYYYYHHMMSSFormattedStringFromMS(milliseconds: Long): String? {
        val date = Date()
        date.time = milliseconds
        return SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date)
    }

    fun getddMMYYYYHHMMFormattedStringFromMS(milliseconds: Long): String? {
        val date = Date()
        date.time = milliseconds
        return SimpleDateFormat("dd-MM-yyyy HH:mm").format(date)
    }

    fun getTodayDDMMYYYYFormattedStr(): String? {
        val time = System.currentTimeMillis()
        return getddMMYYYYHHMMSSFormattedStringFromMS(time)
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val hours = c[Calendar.HOUR_OF_DAY]
        val minutes = c[Calendar.MINUTE]
        val time1 = "$hours:$minutes"
        val time = "" + getTime12Minute(time1)
        if (AppPreference.language.equals("bn")) {
            return time.getNumberInBangla()
        }
        return time
    }

    fun getTime12(time: String): String? {
        var hour12 = ""
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val _12HourSDF = SimpleDateFormat("hh:mm", Locale.ENGLISH)
            val _24HourDt = _24HourSDF.parse(time)
            hour12 = _12HourSDF.format(_24HourDt)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return hour12
    }

    fun getTime12Minute(time: String): String? {
        var hour12 = ""
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val _12HourSDF = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
            val _24HourDt = _24HourSDF.parse(time)
            hour12 = _12HourSDF.format(_24HourDt)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return hour12
    }

    private fun getAMorPM(hours: Int): String? {
        return if (hours < 5) {
            "AM"
        } else if (hours < 12) {
            "AM"
        } else if (hours < 14) {
            "PM"
        } else if (hours < 18) {
            "PM"
        } else if (hours < 23) {
            "PM"
        } else "AM"
    }

    fun getDhuhrAmOrPm(s: String, context: Context): String? {
        val splitTime = s.split(":".toRegex()).toTypedArray()
        val namazHourFajr = splitTime[0]
        val namazminuteFajr = splitTime[1]

        return if (namazHourFajr == "11" || namazHourFajr == "১১") {
            context.getString(R.string.txt_am)
        } else {
            context.getString(R.string.txt_pm)
        }


    }

    fun getBanglaWeekName(index: Int, context: Context): String? {
        val weekName = context.resources.getStringArray(R.array.week_name)
        return if (weekName.size > index) {
            weekName[index]
        } else null
    }

    fun getBanglaMonthName(index: Int, context: Context): String? {
        val monthName = context.resources.getStringArray(R.array.month_name)
        return if (monthName.size > index) {
            monthName[index]
        } else null
    }

    fun getNumberByLocale(string: String): String? {
        var number: Array<Char?>

        number = if (AppPreference.language
                .equals("en")
        ) {
            arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        } else {
            arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        }
        val eng_number = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val values = StringBuilder()
        val character = string.toCharArray()
        for (c1 in character) {
            var c: Char? = null
            for (j in eng_number.indices) {
                if (c1 == eng_number[j]) {
                    c = number[j]
                    break
                } else {
                    c = c1
                }
            }
            values.append(c)
        }
        return values.toString()
    }

    fun getPrizeSerial(number: Int): String {
        when (number) {
            1 -> return "১ম"
            2 -> return "২য়"
            3 -> return "৩য়"
        }
        return ""
    }

    fun getNumber(number: Int): String? {
        return if (number < 10) {
            "0$number"
        } else number.toString()
    }

    fun getAdressWithCityName(context: Context): String {

        var locationName: String = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val latitude: Double = AppPreference.getUserCurrentLocation().lat!!
        val longitude: Double = AppPreference.getUserCurrentLocation().lng!!
        try {
            if (geocoder.getFromLocation(latitude, longitude, 1)?.size != 0) {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.size!! > 0) {
                    val localityName = addresses.get(0).thoroughfare
                    val cityName = addresses.get(0).locality
                    if (localityName != null && cityName != null) {

                        locationName = "$localityName , $cityName"
                    } else {
                        locationName = ""
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return locationName
    }

    fun getCountryName(context: Context): String {

        var countryName = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val latitude: Double = AppPreference.getUserCurrentLocation().lat!!
        val longitude: Double = AppPreference.getUserCurrentLocation().lng!!
        Log.e("lat","$latitude long$longitude")
        try {
            if (geocoder.getFromLocation(latitude, longitude, 1)?.size != 0) {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (addresses?.size!! > 0 && !addresses.isNullOrEmpty()) {
                    countryName = addresses[0].countryName
                    if (countryName != null) {
                        countryName = countryName.uppercase()
                    } else {
                        countryName =
                            "Not Found"
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return countryName
    }

    fun incrementDateByOne(date: Date): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, 1)
        return c.time
    }
    fun incrementDateByThirty(date: Date): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, 30)
        return c.time
    }

    fun decrementDateByOne(date: Date): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, -1)
        return c.time
    }


    fun getDurationString(sec: Int): String? {
        var seconds = sec
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        seconds = seconds % 60
        val formattedString = when (sec >= 3600) {
            true -> "${twoDigitString(hours)}:${twoDigitString(minutes)}:${twoDigitString(seconds)}"
            else -> "${twoDigitString(minutes)}:${twoDigitString(seconds)}"
        }

        return formattedString
    }

    private fun twoDigitString(number: Int): String {
        if (number == 0) {
            return "00"
        }
        return if (number / 10 == 0) {
            "0$number"
        } else number.toString()
    }


}

fun String.toHr(): Int {
    val arr = this.split(":")
    return arr[0].toInt()
}

fun String.toMin(): Int {
    val arr = this.split(":")
    return arr[1].toInt()
}

fun String.toSec(): Int {
    return "00".toInt()
}

fun Long.to24HrFormatMs(): Long {
    var format: DateFormat = SimpleDateFormat("hh:mm a")
    var date = Date()
    date.time = this
    val dateStr = format.format(date)
    if (dateStr.contains("PM")) {
        return this + TWELVE_HOURS_IN_MS
    }

    return this
}