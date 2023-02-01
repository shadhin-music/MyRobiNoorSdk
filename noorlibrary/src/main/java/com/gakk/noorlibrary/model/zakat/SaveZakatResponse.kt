package com.gakk.noorlibrary.model.zakat


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
class SaveZakatResponse(
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
    @SerializedName("ZakatModel")
    @Expose
    var zakatModel: ZakatModel?
)