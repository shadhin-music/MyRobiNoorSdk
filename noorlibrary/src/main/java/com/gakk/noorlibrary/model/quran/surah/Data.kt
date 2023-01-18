package com.gakk.noorlibrary.model.quran.surah

import androidx.annotation.Keep
import com.gakk.noorlibrary.Noor
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.getLocalisedDuration
import java.io.Serializable

@Keep
data class Data(

    @SerializedName("about")
    val about: String? = null,

    @SerializedName("ayats")
    val ayats: String? = null,

    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String? = null,

    @SerializedName("contentUrl")
    val contentUrl: String? = null,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("createdOn")
    val createdOn: String? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("nameInArabic")
    val nameInArabic: String? = null,

    @SerializedName("nameMeaning")
    val nameMeaning: String? = null,

    @SerializedName("order")
    val order: Int,

    @SerializedName("origin")
    val origin: String? = null,

    @SerializedName("pronunciation")
    val pronunciation: String? = null,

    @SerializedName("totalAyat")
    val totalAyat: Int? = null,

    @SerializedName("updatedBy")
    val updatedBy: String? = null,

    @SerializedName("updatedOn")
    val updatedOn: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    @SerializedName("userFavouritedThis")
    var userFavouritedThis: Boolean? = null

) : Serializable {
    var surahNumber: String
        get() {
            return TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(order)!!)!!
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

    var ayahCountWithPrefix: String
        get() {
            return "${Noor.appContext?.resources?.getString(R.string.ayah)} ${
                TimeFormtter.getNumberByLocale(
                    totalAyat.toString()
                )
            }"
        }
        set(value) {
            ayahCountWithPrefix = value
        }
    var durationLocalised: String
        get() {
            return duration?.getLocalisedDuration() ?: "0"
        }
        set(value) {
            durationLocalised = value
        }

    var isSurahFavByThisUser: Boolean
        get() {
            return userFavouritedThis!!
        }
        set(value) {
            userFavouritedThis = value
        }

    var audioUrl: String
        get() {
            return "${contentBaseUrl!!}/${contentUrl!!}"
        }
        set(value) {
            audioUrl = value
        }

    var durationInMs: Long
        get() {
            return TimeFormtter.getMsFromHHMMSS(duration!!)
        }
        set(value) {
            durationInMs = value
        }
}



