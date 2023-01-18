package com.gakk.noorlibrary.model.video.category

import androidx.annotation.Keep

@Keep
data class VideosByCategoryApiResponse(
    val `data`: MutableList<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)