package com.gakk.noorlibrary.extralib.customcalender.listeners

import com.gakk.noorlibrary.extralib.customcalender.EventDay


/**
 * This interface is used to handle clicks on calendar cells
 *
 * Created by Applandeo Team.
 */

interface OnDayClickListener {
    fun onDayClick(eventDay: EventDay)
}
