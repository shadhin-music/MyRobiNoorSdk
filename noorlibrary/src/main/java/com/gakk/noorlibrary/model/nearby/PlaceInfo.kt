package com.gakk.noorlibrary.model.nearby

import com.google.gson.annotations.SerializedName
import com.gakk.noorlibrary.model.UserLocation

class PlaceInfo {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("location")
    var location: UserLocation? = null

    @SerializedName("placeLocation")
    var placeLocation: Location? = null
}