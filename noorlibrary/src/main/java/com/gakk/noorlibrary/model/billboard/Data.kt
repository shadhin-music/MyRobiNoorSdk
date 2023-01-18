package com.gakk.noorlibrary.model.billboard

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class Data(
    @SerializedName("about")
    val about: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("categoryName")
    val categoryName: String,

    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String? = null,

    @SerializedName("createdBy")
    val createdBy: String,

    @SerializedName("createdOn")
    val createdOn: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("language")
    val language: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: Int,
    @SerializedName("updatedBy")
    val updatedBy: String,
    @SerializedName("updatedOn")
    val updatedOn: String

) : Serializable {
    var fullImageUrl: String?
        get() = "$contentBaseUrl/$imageUrl"
        set(value) {
            fullImageUrl = value
        }
}