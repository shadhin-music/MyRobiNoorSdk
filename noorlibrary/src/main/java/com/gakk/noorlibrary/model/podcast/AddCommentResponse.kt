package com.gakk.noorlibrary.model.podcast


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddCommentResponse(
    @SerializedName("data")
    val `data`: Any?,
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
)