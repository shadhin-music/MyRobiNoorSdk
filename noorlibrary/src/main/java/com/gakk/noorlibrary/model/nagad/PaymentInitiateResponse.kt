package com.gakk.noorlibrary.model.nagad


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PaymentInitiateResponse(
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    @SerializedName("paymentUrl")
    val paymentUrl: String?
)