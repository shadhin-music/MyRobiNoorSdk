package com.gakk.noorlibrary.extralib.azan

/**
 * This class is used to round prayer times (seconds).
 */
class Rounding {
    companion object {

        /**
         * Normal Rounding. If seconds are equal to 30 or above, add 1 minute. Sets "Time.seconds" to zero.
         */
        val NORMAL = Rounding()

        /**
         * Special Rounding. Similar to normal rounding but we always round down for Shurooq and Imsaak times. (default)
         */
        val SPECIAL = Rounding()

        /**
         * Aggressive Rounding. Similar to Special Rounding but we add 1 minute if the seconds value are equal to 1 second or more.
         */
        val AGRESSIVE = Rounding()
    }

}
