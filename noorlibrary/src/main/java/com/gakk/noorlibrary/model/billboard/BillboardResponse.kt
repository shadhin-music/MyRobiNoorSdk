package com.gakk.noorlibrary.model.billboard

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BillboardResponse(

    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Any,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int
)