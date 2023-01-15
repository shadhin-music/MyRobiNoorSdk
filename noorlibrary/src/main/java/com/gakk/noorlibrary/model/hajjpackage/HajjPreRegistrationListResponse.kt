package com.gakk.noorlibrary.model.hajjpackage


import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.R

data class HajjPreRegistrationListResponse(
    @SerializedName("data")
    val `data`: MutableList<Data>,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("totalPage")
    val totalPage: Int?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
) {
    data class Data(
        @SerializedName("dateofBirth")
        val dateofBirth: String?,
        @SerializedName("docNumber")
        val docNumber: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("permanentAddress")
        val permanentAddress: String?,
        @SerializedName("phoneNumber")
        val phoneNumber: String?,
        @SerializedName("preRegistrationNo")
        val preRegistrationNo: Any?,
        @SerializedName("presentAddress")
        val presentAddress: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("trackingNo")
        val trackingNo: String?,
        @SerializedName("email")
        val email: String?
    ) {
        var statusPayment: String?
            get() {
                if (status.equals("Paid") || status.equals("Registered")) {
                    return "রিফান্ড রিকোয়েস্ট"
                } else {
                    return status
                }

            }
            set(value) {
                statusPayment = value
            }

        var isEnableBtn: Boolean
            get() {
                return status.equals("Paid") || status.equals("Registered")|| status.equals("Pending")
            }
            set(value) {
                isEnableBtn = value
            }


        var imageResource: Int

            get() {
                if (status.equals("Paid") || status.equals("Registered")) {
                    return R.drawable.border_rounded_green
                } else {
                    return R.drawable.bg_border_rounded_disabled
                }

            }
            set(value) {
                imageResource = value
            }
    }
}