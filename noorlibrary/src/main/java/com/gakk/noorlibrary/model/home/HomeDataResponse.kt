package com.gakk.noorlibrary.model.home

import com.google.gson.annotations.SerializedName

data class HomeDataResponse(
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