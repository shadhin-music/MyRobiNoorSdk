package com.gakk.noorlibrary.model.home

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("category")
    val category: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("contentId")
    val contentId: String,
    @SerializedName("contentName")
    val contentName: String,
    @SerializedName("contentOrder")
    val contentOrder: Int,
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("contentUrl")
    val contentUrl: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("subcategory")
    val subcategory: String,
    @SerializedName("subcategoryName")
    val subcategoryName: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("textInArabic")
    val textInArabic: String
) {
    val fullImageUrl: String
        get() = imageUrl
}