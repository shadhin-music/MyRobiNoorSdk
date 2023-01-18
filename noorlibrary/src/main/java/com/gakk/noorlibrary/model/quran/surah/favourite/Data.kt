package com.gakk.noorlibrary.model.quran.surah.favourite

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(

    @SerializedName("about")
    val about: String,

    @SerializedName("createdBy")
    val createdBy: String,

    @SerializedName("createdOn")
    val createdOn: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("language")
    val language: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("surahId")
    val surahId: String,

    @SerializedName("updatedBy")
    val updatedBy: String,

    @SerializedName("updatedOn")
    val updatedOn: String

)