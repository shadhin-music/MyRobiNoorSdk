package com.gakk.noorlibrary.model.subcategory

import android.os.Parcelable
import androidx.annotation.Keep
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Data(
    val about: String?=null,
    val category: String?=null,
    val categoryName: String?=null,
    val code: String?=null,
    val contentBaseUrl: String?=null,
    val createdBy: String?=null,
    val createdOn: String?=null,
    val id: String?=null,
    val imageUrl: String?=null,
    val isActive: Boolean?=null,
    val language: String?=null,
    val name: String?=null,
    val order: Int?=null,
    val totalContent: Int?=null,
    val updatedBy: String?=null,
    val updatedOn: String?=null,
    val userFavouritedThis: Boolean?=null
): Parcelable{

    @IgnoredOnParcel
    var fullImageUrl:String?
    get()="$contentBaseUrl/$imageUrl"
    set(value) {
        fullImageUrl=value
    }

    @IgnoredOnParcel
    var duaCountFormatted:String
    get()="$totalContent ${Noor.appContext?.resources?.getString(R.string.text_doa_count)}"
    set(value) {
        duaCountFormatted=value
    }
}