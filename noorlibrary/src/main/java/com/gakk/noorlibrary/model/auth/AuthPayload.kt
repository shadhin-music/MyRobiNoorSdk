package com.gakk.noorlibrary.model.auth


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class AuthPayload(
    @SerializedName("MobileNumber")
    @Expose
    var mobileNumber: String? = null
)