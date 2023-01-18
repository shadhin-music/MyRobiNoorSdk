package com.gakk.noorlibrary.model.quranSchool


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class QuranSchoolResponse(
    @SerializedName("data")
    val items: List<QuranSchoolModel>?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("totalRecords")
    val totalRecords: Int?

)