package com.gakk.noorlibrary.model.roza

import androidx.annotation.Keep

@Keep
data class IftarAndSheriTimeforBD(
    val `data`: List<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalPage: Int,
    val totalRecords: Int
)