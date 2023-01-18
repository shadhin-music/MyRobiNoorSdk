package com.gakk.noorlibrary.model.quran.ayah

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.util.TimeFormtter

@Keep
data class Data(
    @SerializedName("about")
    val about: String,

    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String,

    @SerializedName("contentUrl")
    val contentUrl: String,

    @SerializedName("createdBy")
    val createdBy: String,

    @SerializedName("createdOn")
    val createdOn: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("language")
    val language: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("pronunciation")
    val pronunciation: String,

    @SerializedName("surah")
    val surah: String,

    @SerializedName("surahId")
    val surahId: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("textInArabic")
    val textInArabic: String,

    @SerializedName("updatedBy")
    val updatedBy: String,

    @SerializedName("updatedOn")
    val updatedOn: String

){
    var ayahNumber:String
        get() {
            return TimeFormtter.getNumberByLocale(order.toString())!!
        }
        set(value) {
            ayahNumber=value
        }
}