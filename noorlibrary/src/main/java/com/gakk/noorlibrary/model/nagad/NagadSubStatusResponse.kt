package com.gakk.noorlibrary.model.nagad


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NagadSubStatusResponse(
    @SerializedName("response")
    val response: String?
)