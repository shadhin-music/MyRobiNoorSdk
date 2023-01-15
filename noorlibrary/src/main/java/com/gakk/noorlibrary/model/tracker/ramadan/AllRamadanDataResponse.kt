package com.gakk.noorlibrary.model.tracker.ramadan

data class AllRamadanDataResponse(
    val `data`: List<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)