package com.gakk.noorlibrary.model.tracker

import androidx.annotation.Keep

@Keep
data class PostPrayerDataResponse(
    val `data`: Data,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)