package com.gakk.noorlibrary.extralib.customcalender.listeners

import com.gakk.noorlibrary.extralib.customcalender.EventDay


/**
 * This interface is used to handle long clicks on calendar cells
 *
 * Created by Applandeo Team.
 */

interface OnDayLongClickListener {
    fun onDayLongClick(eventDay: EventDay)
}