package com.gakk.noorlibrary.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CommonApiResponse(
    @SerializedName("data")
    val `data`: Any?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("totalPage")
    val totalPage: Int?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)