package com.gakk.noorlibrary.model.home

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("items")
    val items: List<Item>? = null,
    @SerializedName("order")
    val order: Float? = null,
    @SerializedName("about")
    val about: String? = null,
    @SerializedName("patchId")
    val patchId: String? = null,
    @SerializedName("patchName")
    val patchName: String? = null,
    @SerializedName("patchViewType")
    val patchViewType: String? = null,
    @SerializedName("patchImageUrl")
    val patchImageUrl: String? = null,
    @SerializedName("contentBaseUrl")
    val contentBaseUrl: String? = null
) {
    val fullImageUrl: String
        get() = "$contentBaseUrl/$patchImageUrl"

}