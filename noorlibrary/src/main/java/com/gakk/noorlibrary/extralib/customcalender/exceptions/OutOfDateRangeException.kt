package com.gakk.noorlibrary.extralib.customcalender.exceptions

/**
 * Created by Applandeo Team.
 */

data class OutOfDateRangeException(override val message: String) : Exception(message)