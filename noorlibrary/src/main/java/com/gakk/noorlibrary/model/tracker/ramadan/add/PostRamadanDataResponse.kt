package com.gakk.noorlibrary.model.tracker.ramadan.add

data class PostRamadanDataResponse(
    val `data`: Data,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)