package com.gakk.noorlibrary.model.quranSchool


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Scholar(
    @SerializedName("about")
    val about: String?,
    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("institute")
    val institute: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("order")
    val order: Int?,
    @SerializedName("title")
    val title: String?,

): Parcelable {

    @IgnoredOnParcel
    var fullImageUrl: String? = ""
        get() = "$contentBaseUrl/$imageUrl"
        set(value) {
            field = value
        }
}