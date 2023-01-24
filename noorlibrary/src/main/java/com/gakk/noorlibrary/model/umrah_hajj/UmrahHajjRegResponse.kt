package com.mcc.noor.model.umrah_hajj


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class UmrahHajjRegResponse(
    @SerializedName("data")
    @Expose
    var `data`: UmrahHajjRegData?,
    @SerializedName("error")
    @Expose
    var error: Any?,
    @SerializedName("message")
    @Expose
    var message: String?,
    @SerializedName("status")
    @Expose
    var status: Int?,
    @SerializedName("totalPage")
    @Expose
    var totalPage: Int?,
    @SerializedName("totalRecords")
    @Expose
    var totalRecords: Int?
)