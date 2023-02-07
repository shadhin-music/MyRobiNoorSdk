package com.gakk.noorlibrary.model.umrah_hajj


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class UmrahHajjPersonalPostModel(
    @SerializedName("DocumentNumber")
    @Expose
    var documentNumber: String?,
    @SerializedName("DocumentType")
    @Expose
    var documentType: String?,
    @SerializedName("Email")
    @Expose
    var email: String?,
    @SerializedName("Gender")
    @Expose
    var gender: String?,
    @SerializedName("Msisdn")
    @Expose
    var msisdn: String?,
    @SerializedName("PassportNumber")
    @Expose
    var passportNumber: String?,
    @SerializedName("umrahPackageId")
    @Expose
    var umrahPackageId: String?,
    @SerializedName("UserName")
    @Expose
    var userName: String?
)