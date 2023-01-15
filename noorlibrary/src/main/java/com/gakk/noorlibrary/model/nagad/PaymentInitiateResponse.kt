package com.gakk.noorlibrary.model.nagad


import com.google.gson.annotations.SerializedName

data class PaymentInitiateResponse(
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    @SerializedName("paymentUrl")
    val paymentUrl: String?
)