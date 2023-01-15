package com.gakk.noorlibrary.model.nearby

import com.google.gson.annotations.SerializedName

data class NearbyResponse(

    @SerializedName("html_attributions") val htmlAttributions: List<Any>? = null,
    @SerializedName("results") val results: List<Result>? = null,
    @SerializedName("status") val status: String
)
