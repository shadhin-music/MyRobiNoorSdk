package com.gakk.noorlibrary.model.quranSchool


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuranSchoolModel(
    @SerializedName("about")
    val about: String?,
    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String?,
    @SerializedName("contentUrl")
    val contentUrl: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("isActive")
    val isActive: Boolean?,
    @SerializedName("isLive")
    val isLive: Boolean?,
    @SerializedName("language")
    val language: String?,
    @SerializedName("liveOn")
    val liveOn: String?,
    @SerializedName("liveUrl")
    val liveUrl: String?,
    @SerializedName("order")
    val order: Int?,
    @SerializedName("scholar")
    val scholar: String?,
    @SerializedName("scholarName")
    val scholarName: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("updatedBy")
    val updatedBy: String?,
    @SerializedName("updatedOn")
    val updatedOn: String?

) : Parcelable