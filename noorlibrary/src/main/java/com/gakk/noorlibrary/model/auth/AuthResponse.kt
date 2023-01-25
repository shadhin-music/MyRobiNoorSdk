package com.gakk.noorlibrary.model.auth


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class AuthResponse(
    @SerializedName("data")
    @Expose
    var `data`: Data? = null,
    @SerializedName("error")
    @Expose
    var error: Any? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("status")
    @Expose
    var status: Int? = null,
    @SerializedName("totalPage")
    @Expose
    var totalPage: Int? = null,
    @SerializedName("totalRecords")
    @Expose
    var totalRecords: Int? = null
) {
    @Keep
    data class Data(
        @SerializedName("imageUrl")
        @Expose
        var imageUrl: String? = null,
        @SerializedName("roles")
        @Expose
        var roles: List<String?>? = null,
        @SerializedName("token")
        @Expose
        var token: String? = null,
        @SerializedName("userId")
        @Expose
        var userId: String? = null,
        @SerializedName("userName")
        @Expose
        var userName: String? = null
    )
}