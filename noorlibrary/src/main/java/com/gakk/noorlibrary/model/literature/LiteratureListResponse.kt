package com.gakk.noorlibrary.model.literature

import com.google.gson.annotations.SerializedName

data class LiteratureListResponse(
    @SerializedName("data")
    val `data`: MutableList<Literature>,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("totalRecords")
    val totalRecords: Int
)