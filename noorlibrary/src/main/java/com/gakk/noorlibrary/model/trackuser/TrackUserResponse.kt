package com.gakk.noorlibrary.model.trackuser


import com.google.gson.annotations.SerializedName

data class TrackUserResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("error")
    var error: Any? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("totalPage")
    var totalPage: Int? = null,
    @SerializedName("totalRecords")
    var totalRecords: Int? = null
) {
    data class Data(
        @SerializedName("createdBy")
        var createdBy: String? = null,
        @SerializedName("createdOn")
        var createdOn: String? = null,
        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("msisdn")
        var msisdn: String? = null,
        @SerializedName("pagename")
        var pagename: String? = null
    )
}