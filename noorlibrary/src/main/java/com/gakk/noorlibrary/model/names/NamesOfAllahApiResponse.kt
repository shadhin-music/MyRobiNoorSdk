package com.gakk.noorlibrary.model.names

import com.google.gson.annotations.SerializedName

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