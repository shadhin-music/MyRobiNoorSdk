package com.gakk.noorlibrary.model.names

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NamesOfAllahApiResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int
)