package com.gakk.noorlibrary.model.podcast


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CommentListResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
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
        @SerializedName("adminUserType")
        val adminUserType: String?=null,
        @SerializedName("categoryId")
        val categoryId: String?=null,
        @SerializedName("commentFavorite")
        val commentFavorite: Boolean?=null,
        @SerializedName("commentId")
        val commentId: String?=null,
        @SerializedName("commentLike")
        var commentLike: Boolean?=null,
        @SerializedName("createdOn")
        val createdOn: String?=null,
        @SerializedName("currentPage")
        val currentPage: Int?=null,
        @SerializedName("imageUrl")
        val imageUrl: String?=null,
        @SerializedName("message")
        val message: String?=null,
        @SerializedName("textContentId")
        val textContentId: String?=null,
        @SerializedName("totalComment")
        val totalComment: Int?=null,
        @SerializedName("totalCommentFavorite")
        val totalCommentFavorite: Int?=null,
        @SerializedName("totalCommentLike")
        var totalCommentLike: Int?=null,
        @SerializedName("totalPage")
        val totalPage: Int?=null,
        @SerializedName("totalReply")
        val totalReply: Int?=null,
        @SerializedName("userName")
        val userName: String?=null
    )
}