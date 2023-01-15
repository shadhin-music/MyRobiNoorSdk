package com.gakk.noorlibrary.model.quranSchool


import com.google.gson.annotations.SerializedName

data class ScholarsResponse(
    @SerializedName("data")
    val scholarsResponse: List<Scholar>?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)