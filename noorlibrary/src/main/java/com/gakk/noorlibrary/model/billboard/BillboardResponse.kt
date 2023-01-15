package com.gakk.noorlibrary.model.billboard

import com.google.gson.annotations.SerializedName

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