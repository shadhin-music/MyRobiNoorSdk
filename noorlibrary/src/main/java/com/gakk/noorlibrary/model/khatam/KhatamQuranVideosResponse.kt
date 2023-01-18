package com.gakk.noorlibrary.model.khatam


import androidx.annotation.Keep
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.model.video.category.Data
import java.io.Serializable

@Keep
data class KhatamQuranVideosResponse(
    @SerializedName("data")
    val `data`: List<Data>,
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
        @SerializedName("address")
        val address: Any?,
        @SerializedName("billBoardImageUrl")
        val billBoardImageUrl: Any?,
        @SerializedName("category")
        val category: String?,
        @SerializedName("categoryName")
        val categoryName: String?,
        @SerializedName("contentBaseUrl")
        val contentBaseUrl: String?,
        @SerializedName("createdBy")
        val createdBy: String?,
        @SerializedName("createdOn")
        val createdOn: String?,
        @SerializedName("exImageFiveUrl")
        val exImageFiveUrl: Any?,
        @SerializedName("exImageFourUrl")
        val exImageFourUrl: Any?,
        @SerializedName("exImageOneUrl")
        val exImageOneUrl: Any?,
        @SerializedName("exImageThreeUrl")
        val exImageThreeUrl: Any?,
        @SerializedName("exImageTwoUrl")
        val exImageTwoUrl: Any?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("imageUrl")
        val imageUrl: Any?,
        @SerializedName("isActive")
        val isActive: Boolean?,
        @SerializedName("language")
        val language: String?,
        @SerializedName("latitude")
        val latitude: Any?,
        @SerializedName("longitude")
        val longitude: Any?,
        @SerializedName("order")
        val order: Int?,
        @SerializedName("pronunciation")
        val pronunciation: Any?,
        @SerializedName("refUrl")
        val refUrl: String?,
        @SerializedName("subcategory")
        val subcategory: Any?,
        @SerializedName("subcategoryName")
        val subcategoryName: Any?,
        @SerializedName("text")
        val text: String?,
        @SerializedName("textInArabic")
        val textInArabic: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("updatedBy")
        val updatedBy: String?,
        @SerializedName("updatedOn")
        val updatedOn: String?,
        @SerializedName("userFavoriteThis")
        val userFavoriteThis: Boolean?,
        var isPlaying: Boolean? = false,
        var isSelected: Boolean? = false
    ) : Serializable {

        var contentFullUrl: String
            get() = "$contentBaseUrl/$refUrl"
            set(value) {
                contentFullUrl = value
            }

        fun toMediaItem() = MediaItem.Builder()
            .setUri(contentFullUrl)
            .setMediaId(id!!)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setDescription(text)
                    .build()
            )
            .build()
    }


}

fun List<KhatamQuranVideosResponse.Data>.toMediaItems() = this.map { it.toMediaItem() }
