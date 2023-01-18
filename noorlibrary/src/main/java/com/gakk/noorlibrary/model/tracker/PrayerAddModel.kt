package com.gakk.noorlibrary.model.tracker

import androidx.annotation.Keep

@Keep
data class PrayerAddModel(
    val id: String? = null,
    val CreatedBy: String? = null,
    val CreatedOn: String,
    val Language: String,
    val SalahStatus: SalahStatus
)
