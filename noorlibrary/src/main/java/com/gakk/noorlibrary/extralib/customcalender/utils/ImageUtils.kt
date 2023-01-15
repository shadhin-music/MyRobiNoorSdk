@file:JvmName("ImageUtils")

package com.gakk.noorlibrary.extralib.customcalender.utils

import android.widget.ImageView

/**
 * This class is used to load event image in a day cell
 *
 * Created by Applandeo Team.
 */

internal fun ImageView.loadImage(eventImage: EventImage) {
    when (eventImage) {
        is EventImage.EventImageDrawable -> setImageDrawable(eventImage.drawable)
        is EventImage.EventImageResource -> setImageResource(eventImage.drawableRes)
        is EventImage.EmptyEventImage -> Unit
    }
}
