package com.gakk.noorlibrary.model.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class Data(
    @SerializedName("address")
    val address: String? = null,

    @SerializedName("appVersion")
    val appVersion: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("countryCode")
    val countryCode: String? = null,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("createdOn")
    val createdOn: String? = null,

    @SerializedName("dob")
    val dob: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("facebookId")
    val facebookId: String? = null,

    @SerializedName("fcmDeviceId")
    val fcmDeviceId: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("googleId")
    val googleId: String? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("isActice")
    val isActice: Boolean? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("linkedinId")
    val linkedinId: String? = null,

    @SerializedName("msisdn")
    val msisdn: String? = null,

    @SerializedName("order")
    val order: Int? = null,

    @SerializedName("osName")
    val osName: String? = null,

    @SerializedName("osVersion")
    val osVersion: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("refreshTokenExpiryTime")
    val refreshTokenExpiryTime: String? = null,

    @SerializedName("registerWith")
    val registerWith: String? = null,

    @SerializedName("socialImageUrl")
    val socialImageUrl: String? = null,

    @SerializedName("telcoProvider")
    val telcoProvider: String? = null,

    @SerializedName("twitterId")
    val twitterId: String? = null,

    @SerializedName("updatedBy")
    val updatedBy: String? = null,

    @SerializedName("updatedOn")
    val updatedOn: String? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("userName")
    val userName: String? = null

) : Parcelable {

    @IgnoredOnParcel
    var fullImageUrl: String?
        get() = "$contentBaseUrl/$imageUrl"
        set(value) {
            fullImageUrl = value
        }
}