package com.gakk.noorlibrary.model.hajjpackage


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HajjPackageRegistrationResponse(
    @SerializedName("data")
    val `data`: Data?,
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
        @SerializedName("about")
        val about: String? = null,
        @SerializedName("createdBy")
        val createdBy: String? = null,
        @SerializedName("createdOn")
        val createdOn: String? = null,
        @SerializedName("dateofBirth")
        val dateofBirth: String? = null,
        @SerializedName("docImageUrl1")
        val docImageUrl1: String? = null,
        @SerializedName("docImageUrl2")
        val docImageUrl2: String? = null,
        @SerializedName("docNumber")
        val docNumber: String? = null,
        @SerializedName("docType")
        val docType: String? = null,
        @SerializedName("gender")
        val gender: String? = null,
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("isActive")
        val isActive: Boolean? = null,
        @SerializedName("language")
        val language: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("order")
        val order: Int? = null,
        @SerializedName("permanentAddress")
        val permanentAddress: String? = null,
        @SerializedName("permanentDistrict")
        val permanentDistrict: String? = null,
        @SerializedName("phoneNumber")
        val phoneNumber: String? = null,
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("preRegistrationNo")
        val preRegistrationNo: String? = null,
        @SerializedName("presentAddress")
        val presentAddress: String? = null,
        @SerializedName("refundRequestBy")
        val refundRequestBy: String? = null,
        @SerializedName("refundRequestOn")
        val refundRequestOn: String? = null,
        @SerializedName("refundedBy")
        val refundedBy: String? = null,
        @SerializedName("refundedOn")
        val refundedOn: String? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("trackingNo")
        val trackingNo: String? = null,
        @SerializedName("updatedBy")
        val updatedBy: String? = null,
        @SerializedName("updatedOn")
        val updatedOn: String? = null
    )
}