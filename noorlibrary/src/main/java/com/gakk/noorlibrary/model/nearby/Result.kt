package com.gakk.noorlibrary.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {
    @SerializedName("geometry")
    @Expose
    var geometry: com.gakk.noorlibrary.model.nearby.Geometry? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

    @SerializedName("rating")
    @Expose
    var rating: Double? = null

    @SerializedName("reference")
    @Expose
    var reference: String? = null

    @SerializedName("scope")
    @Expose
    var scope: String? = null

    @SerializedName("types")
    @Expose
    var types: List<String>? = null

    @SerializedName("user_ratings_total")
    @Expose
    var userRatingsTotal: Int? = null

    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null
}