package com.gakk.noorlibrary.model.quran.surahDetail

import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.getLocalisedDuration
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("about")
    val about: String,

    @SerializedName("ayats")
    val ayats: String,

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
    val imageUrl: Any,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("language")
    val language: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("nameInArabic")
    val nameInArabic: String,

    @SerializedName("nameMeaning")
    val nameMeaning: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("origin")
    val origin: String,

    @SerializedName("pronunciation")
    val pronunciation: String,

    @SerializedName("totalAyat")
    val totalAyat: Int,

    @SerializedName("updatedBy")
    val updatedBy: String,

    @SerializedName("updatedOn")
    val updatedOn: String,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("userFavouritedThis")
    var userFavouritedThis: Boolean
) {
    var surahNumber: String
        get() {
            return TimeFormtter.getNumberByLocale(order.toString())!!
        }
        set(value) {
            surahNumber = value
        }
    var surahBasicInfo: String
        get() {
            if (nameMeaning != null) {
                return "${nameMeaning} • ${TimeFormtter.getNumberByLocale(totalAyat.toString())} • $origin"
            } else {
                return "${TimeFormtter.getNumberByLocale(totalAyat.toString())} • $origin"
            }

        }
        set(value) {
            surahBasicInfo = value
        }

    var isSurahFavByThisUser: Boolean
        get() = userFavouritedThis
        set(value) {
            userFavouritedThis = value
        }

    var durationLocalised: String
        get() {
            return duration?.getLocalisedDuration()!!
        }
        set(value) {
            durationLocalised = value
        }

}