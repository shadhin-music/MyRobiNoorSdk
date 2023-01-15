package com.gakk.noorlibrary.model.tracker

data class AllPrayerDataResponse(
    val `data`: List<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)