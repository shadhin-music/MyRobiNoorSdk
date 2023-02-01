package com.gakk.noorlibrary.model.zakat


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class ZakatDelResponse(
    @SerializedName("data")
    @Expose
    var `data`: String?,
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