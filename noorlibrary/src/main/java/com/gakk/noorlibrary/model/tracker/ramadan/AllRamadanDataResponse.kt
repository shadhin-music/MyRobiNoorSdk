package com.gakk.noorlibrary.model.tracker.ramadan

import androidx.annotation.Keep

@Keep
data class AllRamadanDataResponse(
    val `data`: List<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)