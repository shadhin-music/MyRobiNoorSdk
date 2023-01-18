package com.gakk.noorlibrary.model.quran.surah.unfavourite

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UnfavouriteResponse(

    @SerializedName("data")
    val `data`: String,

    @SerializedName("error")
    val error: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("totalRecords")
    val totalRecords: Int
)