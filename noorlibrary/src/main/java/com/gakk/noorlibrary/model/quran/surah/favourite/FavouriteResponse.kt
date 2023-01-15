package com.gakk.noorlibrary.model.quran.surah.favourite

import com.google.gson.annotations.SerializedName

data class FavouriteResponse(

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