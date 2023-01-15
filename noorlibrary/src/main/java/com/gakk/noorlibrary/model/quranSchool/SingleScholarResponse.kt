package com.gakk.noorlibrary.model.quranSchool


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SingleScholarResponse(
    @SerializedName("data")
    val scholar: Scholar?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)