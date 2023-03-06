package com.gakk.noorlibrary.roza

import com.gakk.noorlibrary.util.HIZRI_YR_OFFSET
import com.gakk.noorlibrary.util.TWENTY_FOUR_HOURS_IN_MS
import com.gakk.noorlibrary.util.TimeFormtter
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.util.*


object CalenderUtil {

    /**
     * @param:milliseconds(Long)
     * @return DAY_OF_WEEK for current MONTH in GEORGIAN Calender
     */
    fun getDayOfWeek(ms:Long):Int{
        var gCal= GregorianCalendar()
        gCal.timeInMillis=ms
        return gCal.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * @param:milliseconds(Long)
     * @return DAY_OF_MONTH for current MONTH in GEORGIAN Calender
     */
    fun getGrgDayOfCurrentMonth(ms:Long):Int{
        var dateStr=TimeFormtter.getddMMYYYYFormattedStringFromMS(ms)
        var dateStrSplitted=dateStr!!.split("-")
        return dateStrSplitted!!.get(0).toInt()
    }

    /**
     * @param:milliseconds(Long)
     * @return DAY_OF_MONTH for Tomorrow in GEORGIAN Calender
     */
    fun getGrgTomorrow(ms:Long):Int{
        var dateStr=TimeFormtter.getddMMYYYYFormattedStringFromMS(ms+ TWENTY_FOUR_HOURS_IN_MS)
        var dateStrSplitted=dateStr!!.split("-")
        return dateStrSplitted!!.get(0).toInt()
    }

    /**
     * @param:milliseconds(Long)
     * @return DAY_OF_MONTH for current MONTH in HIZRI Calender
     */
    fun getHzrDayOfCurrentMonth(ms:Long):Int{

        var gCal= GregorianCalendar()
        gCal.timeInMillis=ms
        var uCal =UmmalquraCalendar()
        uCal.time=gCal.time
        return uCal[Calendar.DAY_OF_MONTH]
    }

    /**
     * returns corresponding date(milliseconds) in Georgian Calender  for First of Ramadan
     */
    fun getFirstRmdnGrgMs():Long{
        var uCal= UmmalquraCalendar(getCurrentHizriYear(),UmmalquraCalendar.RAMADHAN,1)
        var gCal= GregorianCalendar()
        gCal.time=uCal.time
        return gCal.timeInMillis
    }
    //provides current Hizri Year From current Time
    fun getCurrentHizriYear():Int{
        var date=TimeFormtter.getddMMYYYYFormattedStringFromMS(System.currentTimeMillis())
        var splitDate=date?.split("-")
        var year=splitDate!!.get(2)
        return year.toInt()- HIZRI_YR_OFFSET
    }

    fun isRamadanNow():Boolean{
        var uCal= UmmalquraCalendar()
//        val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        if (date == "2022-04-02"){
//            return false
//
//        }
        return uCal[Calendar.MONTH]==UmmalquraCalendar.RAMADHAN
    }
//    @RequiresApi(Build.VERSION_CODES.N)
//    fun isRamadanNow2():Boolean{
//        var uCal= IslamicCalendar()
//        return uCal[Calendar.MONTH]==IslamicCalendar.RAMADAN
//    }
    fun isShabanNow():Boolean{
        var uCal= UmmalquraCalendar()
        return uCal[Calendar.MONTH]==UmmalquraCalendar.SHAABAN
    }

    fun getDayOfHizriMonth():Int{
        var uCal= UmmalquraCalendar()
        return uCal[Calendar.DAY_OF_MONTH]
    }
    fun getDayOfHizriMonth2():Int{
        var uCal= UmmalquraCalendar()
        return uCal[Calendar.DAY_OF_MONTH]
    }

    fun daysLeftTillRamadan():Long{
        var uCal= UmmalquraCalendar()
        var todayInMs=uCal.timeInMillis
        uCal= UmmalquraCalendar(getCurrentHizriYear(),UmmalquraCalendar.RAMADHAN,1)
        var firstRamadanInMs=uCal.timeInMillis
        var daysLeftInMs  =firstRamadanInMs-todayInMs
        return daysLeftInMs / TWENTY_FOUR_HOURS_IN_MS
    }

    //this func is call only for 2022
    fun daysLeftTillRamadanFor2022():Long{
        var uCal= UmmalquraCalendar()
        var todayInMs=uCal.timeInMillis
        var date = TimeFormtter.MillisecondFromDateString("2023-03-24T00:00:00")

        //uCal= UmmalquraCalendar(getCurrentHizriYear(),UmmalquraCalendar.RAMADHAN,1)
//        var firstRamadanInMs=uCal.timeInMillis
        var firstRamadanInMs=date
        var daysLeftInMs  = firstRamadanInMs?.minus(todayInMs)
        return daysLeftInMs?.div(TWENTY_FOUR_HOURS_IN_MS) ?: 0
    }


}