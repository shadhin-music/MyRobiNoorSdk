package com.gakk.noorlibrary.model.hajjtracker


import com.google.gson.annotations.SerializedName

data class HajjSharingListResponse(
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
        @SerializedName("trackerName")
        val trackerName: String?,
        @SerializedName("trackerPhone")
        val trackerPhone: String?
    ){
        var fullImageUrl: String?
            get() = "$contentBaseUrl/$imageUrl"
            set(value) {
                fullImageUrl = value
            }
    }
}