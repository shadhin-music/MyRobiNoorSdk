package com.gakk.noorlibrary.model.umrah_hajj


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class UmrahPaymentStatus(
    @SerializedName("TrackingNumber")
    @Expose
    var trackingNumber: String,
    @SerializedName("TransactionNumber")
    @Expose
    var transactionNumber: String
)