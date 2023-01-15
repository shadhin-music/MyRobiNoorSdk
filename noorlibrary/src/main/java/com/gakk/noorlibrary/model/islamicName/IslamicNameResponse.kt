package com.gakk.noorlibrary.model.islamicName

import com.google.gson.annotations.SerializedName

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/18/2021, Sun
 */
data class IslamicNameResponse(
    @SerializedName("data")
    val `data`: MutableList<IslamicName>,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int
)