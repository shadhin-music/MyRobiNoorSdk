package com.gakk.noorlibrary.model.subs


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CheckSubResponse(
    @SerializedName("Reg_Date")
    val regDate: String?,
    @SerializedName("Reg_Status")
    val regStatus: String?
)