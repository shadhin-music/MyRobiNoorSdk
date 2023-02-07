package com.gakk.noorlibrary.model

import androidx.annotation.Keep
import com.gakk.noorlibrary.util.IMAGES_BASE_URL

@Keep
data class ImageFromOnline(val imageName: String) {

    val fullImageUrl: String
        get() = "$IMAGES_BASE_URL$imageName"
}
