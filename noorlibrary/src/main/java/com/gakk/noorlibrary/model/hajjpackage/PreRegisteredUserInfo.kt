package com.gakk.noorlibrary.model.hajjpackage

import androidx.annotation.Keep

@Keep
data class PreRegisteredUserInfo(
    val trackingNo: String?,
    val name: String?,
    val email: String?
)
