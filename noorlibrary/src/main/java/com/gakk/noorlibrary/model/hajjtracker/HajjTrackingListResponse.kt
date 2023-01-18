package com.gakk.noorlibrary.model.hajjtracker


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HajjTrackingListResponse(
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
        @SerializedName("contentBaseUrl")
        val contentBaseUrl: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("imageUrl")
        val imageUrl: String?,
        @SerializedName("sharerPhone")
        val sharerPhone: String?,
        @SerializedName("trackerName")
        val trackerName: String?
    ){
        var fullImageUrl: String?
            get() = "$contentBaseUrl/$imageUrl"
            set(value) {
                fullImageUrl = value
            }
    }
}