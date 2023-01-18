package com.gakk.noorlibrary.model.quran.ayah

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AyahsBySurah(

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