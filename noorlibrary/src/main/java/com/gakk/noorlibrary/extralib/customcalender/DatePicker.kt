package com.gakk.noorlibrary.extralib.customcalender

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.extralib.customcalender.utils.CalendarProperties
import com.gakk.noorlibrary.extralib.customcalender.utils.isMonthAfter
import com.gakk.noorlibrary.extralib.customcalender.utils.isMonthBefore
import com.gakk.noorlibrary.extralib.customcalender.utils.midnightCalendar

/**
 * This class is responsible for creating DatePicker dialog.
 *
 * Created by Applandeo Team.
 */

class DatePicker(
        private val context: Context,
        private val calendarProperties: CalendarProperties
) {

    fun show(): DatePicker {
        val view = LayoutInflater.from(context).inflate(R.layout.date_picker_dialog, null)

        if (calendarProperties.pagesColor != 0) {
            view.setBackgroundColor(calendarProperties.pagesColor)
        }

        setTodayButtonVisibility(view.findViewById(R.id.todayButton))
        setDialogButtonsColors(view.findViewById(R.id.negativeButton), view.findViewById(R.id.todayButton))
        setOkButtonState(calendarProperties.calendarType == CalendarView.ONE_DAY_PICKER, view.findViewById(R.id.positiveButton))

        setDialogButtonsTypeface(view)

        calendarProperties.onSelectionAbilityListener = { enabled ->
            setOkButtonState(enabled, view.findViewById(R.id.positiveButton))
        }

        val calendarView = CalendarView(context = context, properties = calendarProperties)

        view.findViewById<FrameLayout>(R.id.calendarContainer).addView(calendarView)

        calendarProperties.calendar?.let {
            runCatching { calendarView.setDate(it) }
        }

        val alertDialog = AlertDialog.Builder(context).create().apply {
            setView(view)
        }

        view.findViewById<AppCompatButton>(R.id.negativeButton).setOnClickListener { alertDialog.cancel() }

        view.findViewById<AppCompatButton>(R.id.positiveButton).setOnClickListener {
            alertDialog.cancel()
            calendarProperties.onSelectDateListener?.onSelect(calendarView.selectedDates)
        }

        view.findViewById<AppCompatButton>(R.id.todayButton).setOnClickListener { calendarView.showCurrentMonthPage() }

        alertDialog.show()

        return this
    }

    private fun setDialogButtonsTypeface(view: View) {
        calendarProperties.typeface?.let { typeface ->
            view.findViewById<AppCompatButton>(R.id.todayButton).typeface = typeface
            view.findViewById<AppCompatButton>(R.id.negativeButton).typeface = typeface
            view.findViewById<AppCompatButton>(R.id.positiveButton).typeface = typeface
        }
    }

    private fun setDialogButtonsColors(negativeButton: AppCompatButton, todayButton: AppCompatButton) {
        if (calendarProperties.dialogButtonsColor != 0) {
            negativeButton.setTextColor(ContextCompat.getColor(context, calendarProperties.dialogButtonsColor))
            todayButton.setTextColor(ContextCompat.getColor(context, calendarProperties.dialogButtonsColor))
        }
    }

    private fun setOkButtonState(enabled: Boolean, okButton: AppCompatButton) {
        okButton.isEnabled = enabled

        if (calendarProperties.dialogButtonsColor == 0) return

        val stateResource = if (enabled) {
            calendarProperties.dialogButtonsColor
        } else {
            R.color.disabledDialogButtonColor
        }

        okButton.setTextColor(ContextCompat.getColor(context, stateResource))
    }

    private fun setTodayButtonVisibility(todayButton: AppCompatButton) {
        calendarProperties.maximumDate?.let {
            if (it.isMonthBefore(midnightCalendar) || it.isMonthAfter(midnightCalendar)) {
                todayButton.visibility = View.GONE
            }
        }
    }
}
