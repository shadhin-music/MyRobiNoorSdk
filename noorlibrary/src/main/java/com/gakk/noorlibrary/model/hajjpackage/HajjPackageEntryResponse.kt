package com.gakk.noorlibrary.model.hajjpackage


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HajjPackageEntryResponse(
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
        val about: Any?,
        @SerializedName("createdBy")
        val createdBy: String?,
        @SerializedName("createdOn")
        val createdOn: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("isActive")
        val isActive: Boolean?,
        @SerializedName("language")
        val language: String?,
        @SerializedName("order")
        val order: Int?,
        @SerializedName("phoneNumber")
        val phoneNumber: String?,
        @SerializedName("updatedBy")
        val updatedBy: Any?,
        @SerializedName("updatedOn")
        val updatedOn: Any?
    )
}