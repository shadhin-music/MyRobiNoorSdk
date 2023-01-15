package com.gakk.noorlibrary.util

import android.content.Context
import com.gakk.noorlibrary.R
import java.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */
class CalendarUtility {
    fun getCurrentHour(): Int {
        return Calendar.getInstance().get(11)
    }

    fun timeFormateChanger12(str: String?): String? {
        if (str == null || str.length <= 2) {
            return str
        }
        val stringSpliter: List<String> = stringSpliter(str, ":")
        var parseInt = stringSpliter[0].toInt()
        val parseInt2 = stringSpliter[1].toInt()
        var str2 = "am"
        if (parseInt > 12) {
            parseInt -= 12
            str2 = "pm"
        } else if (parseInt == 0) {
            parseInt = 12
        }
        return get01Valu(parseInt.toString() + "").toString() + ":" + get01Valu(parseInt2.toString() + "") + " " + str2
    }

    fun get01Valu(str: String?): String? {
        return if (str == null || str.isEmpty() || str.length != 1) {
            str
        } else "0$str"
    }

    fun stringSpliter(str: String, str2: String): List<String> {
        val sp = str.split("\\s*$str2\\s*")
        return sp
    }

    fun convertHour2Digri(str: String, str2: String): String {
        return try {
            val parseFloat = str.toFloat()
            val parseFloat2 = str2.toFloat()
            (parseFloat * 30.0f + parseFloat2 / 2.0f).toString() + "," + parseFloat2 * 6.0f
        } catch (unused: Exception) {
            ""
        }
    }

    fun getIslamicMonthName(i: Int): String {
        return if (i < 0 || i > 11) {
            ""
        } else when (i) {
            0 -> "মুহররম"
            1 -> "সফর"
            2 -> "রবিউল আউয়াল"
            3 -> "রবিউস সানি"
            4 -> "জমাদিউল আউয়াল"
            5 -> "জমাদিউস সানি"
            6 -> "রজব"
            7 -> "শাবান"
            8 -> "রমজান"
            9 -> "শাওয়াল"
            10 -> "জিলক্বদ"
            11 -> "জিলহজ্জ"
            else -> ""
        }
    }

    fun getIslamicMonthNameEn(i: Int): String {
        return if (i < 0 || i > 11) {
            ""
        } else when (i) {
            0 -> "Muharram"
            1 -> "Safar"
            2 -> "Rabi'al-awwal"
            3 -> "Rabi'ath-thani"
            4 -> "Jumada al-Ula"
            5 -> "Jumada al-akhirah"
            6 -> "Rajab"
            7 -> "Sha‘ban"
            8 -> "Ramadan"
            9 -> "Shawwal"
            10 -> "Dhu al-Qa'dah"
            11 -> "Dhu al-Hijjah"
            else -> ""
        }
    }

    fun getWeekName(calendar: Calendar, context: Context): String {
        val i = calendar[7]
        val stringArray: Array<String> =
            context.resources.getStringArray(R.array.week_name)
        if (1 == i) {
            return stringArray[0]
        }
        if (2 == i) {
            return stringArray[1]
        }
        if (3 == i) {
            return stringArray[2]
        }
        if (4 == i) {
            return stringArray[3]
        }
        if (5 == i) {
            return stringArray[4]
        }
        if (6 == i) {
            return stringArray[5]
        }
        return if (7 == i) {
            stringArray[6]
        } else ""
    }

    fun getMonthName(context: Context, i: Int): String? {
        val stringArray = context.resources.getStringArray(R.array.month_name)
        return if (i < stringArray.size) {
            stringArray[i]
        } else null
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance()[1]
    }
}