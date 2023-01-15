package com.gakk.noorlibrary.model.tracker

data class PrayerAddModel(
    val id: String? = null,
    val CreatedBy: String? = null,
    val CreatedOn: String,
    val Language: String,
    val SalahStatus: SalahStatus
)
