package com.gakk.noorlibrary.model.ssl


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SslPaymentInitiateResponse(
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    @SerializedName("GatewayPageURL")
    val gatewayPageURL: String?,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("subscription_id")
    val subscriptionId: Any?
)