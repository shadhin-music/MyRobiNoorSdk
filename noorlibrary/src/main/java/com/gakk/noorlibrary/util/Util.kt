package com.gakk.noorlibrary.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/8/2021, Thu
 */
object Util {
    private lateinit var mProgressDialog: ProgressDialog


    fun showProgressDialog(context: Context?, title: String?, message: String?) {
        mProgressDialog = ProgressDialog.show(context, null, null, true, true)
        mProgressDialog.setContentView(R.layout.progress_layout)
        mProgressDialog.setTitle(title)
        mProgressDialog.setMessage(message)
        if (mProgressDialog.getWindow() != null) {
            mProgressDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        mProgressDialog.show()
    }

    fun hide() {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
            //mProgressDialog = null
        }
    }

    fun checkSub(): Boolean {

        if (AppPreference.subDaily || AppPreference.subFifteenDays || AppPreference.subWeeklyRobi || AppPreference.subMonthlyRobi
            || AppPreference.subMonthlySsl || AppPreference.subHalfYearlySsl || AppPreference.subYearlySsl
            || AppPreference.subWeeklyRobiOnDemand || AppPreference.subMonthlyRobiOnDemand
        ) {
            return true
        } /*else if (isRobiNumber(AppPreference.userNumber!!)) {
            return true
        }*/
        return false
    }

    fun checkSelectedDate(selectedDate: String?): Boolean {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val dateToday = Date()
        try {
            val formatedstartDate = dateFormat.parse(selectedDate)
            return if (dateToday.compareTo(formatedstartDate) < 0) {
                false
            } else if (dateToday.compareTo(formatedstartDate) > 0) {
                true
            } else {
                true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return true
    }

    fun checkTodayDate(selectedDate: String?, todayDate: String?): Boolean {
        val objSDF = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val dt_1 = objSDF.parse(todayDate)
        val dt_2 = objSDF.parse(selectedDate)

        var value = false

        if (dt_1.compareTo(dt_2) > 0) {
            println("Date 1 occurs after Date 2")
            value = false
        } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
        else if (dt_1.compareTo(dt_2) < 0) {
            println("Date 1 occurs before Date 2")
            value = false
        } // compareTo method returns the value less than 0 if this Date is before the Date argument;
        else if (dt_1.compareTo(dt_2) == 0) {
            println("Both are same dates")
            value = true
        } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
        else {
            value = false
            println("You seem to be a time traveller !!")
        }

        return value
    }

    fun getLastDayOfTheMonth(date: String?): String {
        var lastDayOfTheMonth = ""
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        try {
            val dt = formatter.parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dt
            calendar.add(Calendar.MONTH, 1)
            calendar[Calendar.DAY_OF_MONTH] = 1
            calendar.add(Calendar.DATE, -1)
            val lastDay = calendar.time
            lastDayOfTheMonth = formatter.format(lastDay)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return lastDayOfTheMonth
    }

    fun getFirstDateOfMonth(date: Date?): Date? {
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.DAY_OF_MONTH] = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
        return cal.time
    }

    fun displayPromptForEnablingGPS(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Turn on GPS for getting accurate info !"
        builder.setMessage(message)
            .setPositiveButton(
                "OK"
            ) { d: DialogInterface, id: Int ->
                activity.startActivity(Intent(action))
                d.dismiss()
            }
            .setNegativeButton(
                "Cancel"
            ) { d: DialogInterface, id: Int -> d.cancel() }
        builder.create().show()
    }
}

fun Long?.abs(): Long {
    return this?.absoluteValue ?: 0L
}

@SuppressLint("MissingPermission")
fun isNetworkConnected(context: Context?): Boolean {
    val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null
}

fun checkOtherNumber(): Boolean {
    val numberUser = AppPreference.userNumber!!
    return (numberUser.startsWith("88016") or numberUser.startsWith("88018"))
}

fun isRobiNumber(): Boolean {
    val numberUser = AppPreference.userNumber!!
    return (numberUser.startsWith("88018"))
}

fun isAirtelNumber(): Boolean {
    val numberUser = AppPreference.userNumber!!
    return (numberUser.startsWith("88016"))
}

fun getOperatorTypeClass(Number: String?): String? {
    var operatorType = ""
    if (Number != null) {
        try {
            val n = 5
            val vendor = Number.substring(4, Math.min(Number.length, n))
            operatorType = if (vendor.contains("9")) {
                "Banglalink"
            } else if (vendor.contains("8")) {
                "Robi"
            } else if (vendor.contains("7")) {
                "GP"
            } else if (vendor.contains("6")) {
                "Airtel"
            } else if (vendor.contains("5")) {
                "Teletalk"
            } else {
                "Unknown Network"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return operatorType
}

suspend inline fun <T> safeApiCall(crossinline responseFunction: suspend () -> T): Resource<T>? {
    return withContext(Dispatchers.IO) {

        Resource.loading(data = null)
        try {
            val response = responseFunction.invoke()
            Resource.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(null, e.message.toString())
        }
    }

}

fun decrementDateByOne(date: Date?): Date? {
    val c = Calendar.getInstance()
    c.time = date
    c.add(Calendar.DATE, -1)
    return c.time
}

fun getBitmapFromView(ctx: Context, view: View): Bitmap? {
    view.layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )

    val dm = ctx.resources.displayMetrics
    view.measure(
        View.MeasureSpec.makeMeasureSpec(
            dm.widthPixels,
            View.MeasureSpec.EXACTLY
        ),
        View.MeasureSpec.makeMeasureSpec(
            dm.heightPixels,
            View.MeasureSpec.EXACTLY
        )
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(
        view.measuredWidth,
        view.measuredHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.layout(view.left, view.top, view.right, view.bottom)
    view.draw(canvas)
    return bitmap
}


fun formatDate(dateString: String): String? {
    try {
        var sd = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
            AppPreference.language?.let { Locale(it) })
        val d: Date = sd.parse(dateString)
        sd = SimpleDateFormat("HH:mm aa • dd MMMM ,yyyy",
            AppPreference.language?.let { Locale(it) })
        return sd.format(d)
    } catch (e: ParseException) {
    }
    return ""
}

fun String.convertTo12HourFormat(): String {
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date24 = sdf24.parse(this)
    return sdf12.format(date24)
}

fun String.convertTo12HourFormatWithoutAMPM(): String {
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdf12 = SimpleDateFormat("hh:mm", Locale.getDefault())
    val date24 = sdf24.parse(this)
    return sdf12.format(date24)
}