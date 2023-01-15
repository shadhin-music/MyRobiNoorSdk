package com.gakk.noorlibrary.model.quran.surahDetail

import com.google.gson.annotations.SerializedName

data class SurahDetailsResponse(

    @SerializedName("data")
    val `data`: Data,

    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("totalRecords")
    val totalRecords: Int
)