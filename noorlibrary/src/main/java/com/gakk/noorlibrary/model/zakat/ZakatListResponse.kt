package com.gakk.noorlibrary.model.zakat

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class ZakatListResponse(
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
    var totalRecords: Int?,
    @SerializedName("data")
    @Expose
    var data: List<ZakatModel>?
)