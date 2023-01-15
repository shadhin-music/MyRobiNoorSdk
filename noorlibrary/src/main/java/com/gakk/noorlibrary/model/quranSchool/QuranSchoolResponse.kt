package com.gakk.noorlibrary.model.quranSchool


import com.google.gson.annotations.SerializedName

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