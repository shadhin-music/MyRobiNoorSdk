package com.gakk.noorlibrary.model.tracker.ramadan.add

import androidx.annotation.Keep

@Keep
data class RamadanAddModel(
    val id: String? = null,
    val CreatedBy: String? = null,
    val CreatedOn: String,
    val Language: String,
    val RamadanStatus: Boolean
)
