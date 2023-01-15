package com.gakk.noorlibrary.model.names

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.util.TimeFormtter
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Data(
    @SerializedName("about")
    val about: String,

    @SerializedName("arabic")
    val arabic: String,

    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String,

    @SerializedName("contentUrl")
    val contentUrl: String,

    @SerializedName("createdBy")
    val createdBy: String,

    @SerializedName("createdOn")
    val createdOn: String,

    @SerializedName("fazilat")
    val fazilat: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("language")
    val language: String,

    @SerializedName("meaning")
    val meaning: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("updatedBy")
    val updatedBy: String,

    @SerializedName("updatedOn")
    val updatedOn: String

) : Parcelable {
    var localisedOrder: String
        get() {
            return TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(order)!!)!!
        }
        set(value) {
            localisedOrder = value
        }

}