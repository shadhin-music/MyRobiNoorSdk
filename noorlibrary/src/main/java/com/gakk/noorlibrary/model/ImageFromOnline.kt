package com.gakk.noorlibrary.model

import com.gakk.noorlibrary.util.IMAGES_BASE_URL

data class ImageFromOnline(val imageName: String) {

    val fullImageUrl: String?
        get() = "$IMAGES_BASE_URL$imageName"
}
