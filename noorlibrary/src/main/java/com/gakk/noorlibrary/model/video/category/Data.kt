package com.gakk.noorlibrary.model.video.category

import androidx.annotation.Keep
import com.gakk.noorlibrary.util.TimeFormtter
import java.io.Serializable

@Keep
data class Data(
    val about: String? = null,
    val age: String? = null,
    val bannerURL: String? = null,
    val category: String? = null,
    val categoryName: String? = null,
    val composer: String? = null,
    val contenTtitle: String? = null,
    val contentBaseUrl: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val description: String? = null,
    val director: String? = null,
    val duration: String? = null,
    val ep: String? = null,
    val epNumber: String? = null,
    val filePath: String? = null,
    val genreAs: String? = null,
    val genreCode: String? = null,
    val id: String? = null,
    val isActive: Boolean? = null,
    val isLove: String? = null,
    val isWish: String? = null,
    val labelCode: String? = null,
    val language: String? = null,
    val lyricist: String? = null,
    val order: Int? = null,
    val playPercent: Int? = null,
    val previewURL: String? = null,
    val publishingDate: String? = null,
    val publishingYear: String? = null,
    val rating: String? = null,
    val singer: String? = null,
    val starring: String? = null,
    val subTitle: String? = null,
    val subcategory: String? = null,
    val subcategoryName: String? = null,
    val type: String? = null,
    val updatedBy: String? = null,
    val updatedOn: String? = null,
    val viewCount: Int? = null,
    var isPlaying: Boolean? = false,
    var isSelected:Boolean? = false
) : Serializable {

    var fullImageUrl: String
        get() = "$contentBaseUrl/$previewURL"
        set(value) {
            contentFullUrl = value
        }
    var contentFullUrl: String
        get() = "$contentBaseUrl/$filePath"
        set(value) {
            contentFullUrl = value
        }

    var miniSummary: String
        get() = "$singer • $publishingYear"
        set(value) {
            miniSummary = value
        }

    var durationFormatted: String
        get() = TimeFormtter.getNumberByLocale(
            TimeFormtter.getDurationString(
                duration?.toInt() ?: 0
            ) ?: "00:00"
        ) ?: "0"
        set(value) {
            durationFormatted = value
        }

}


