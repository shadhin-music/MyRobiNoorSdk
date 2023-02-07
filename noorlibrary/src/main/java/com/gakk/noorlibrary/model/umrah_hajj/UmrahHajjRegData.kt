package com.gakk.noorlibrary.model.umrah_hajj


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class UmrahHajjRegData(
    @SerializedName("msisdn")
    @Expose
    var msisdn: String?,
    @SerializedName("passportNumber")
    @Expose
    var passportNumber: String?,
    @SerializedName("paymentStatus")
    @Expose
    var paymentStatus: String?,
    @SerializedName("trackingNumber")
    @Expose
    var trackingNumber: String?,
    @SerializedName("userName")
    @Expose
    var userName: String?,

    @SerializedName("registrationDate")
    @Expose
    var registrationDate: String?,

    @SerializedName("umrahPackTitle")
    @Expose
    var umrahPackTitle: String?,

    @SerializedName("packPrice")
    @Expose
    var packPrice: String?,


)