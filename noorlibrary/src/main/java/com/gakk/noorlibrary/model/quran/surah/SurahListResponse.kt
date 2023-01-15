package com.gakk.noorlibrary.model.quran.surah

import com.google.gson.annotations.SerializedName

data class SurahListResponse(
    @SerializedName("data")
    val `data`: MutableList<Data>,

    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("totalRecords")
    val totalRecords: Int
)