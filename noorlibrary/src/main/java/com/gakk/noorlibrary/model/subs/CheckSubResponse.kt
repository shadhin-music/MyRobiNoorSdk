package com.gakk.noorlibrary.model.subs


import com.google.gson.annotations.SerializedName

data class CheckSubResponse(
    @SerializedName("Reg_Date")
    val regDate: String?,
    @SerializedName("Reg_Status")
    val regStatus: String?
)