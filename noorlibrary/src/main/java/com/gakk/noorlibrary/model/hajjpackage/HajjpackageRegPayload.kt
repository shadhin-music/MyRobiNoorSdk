package com.gakk.noorlibrary.model.hajjpackage

import androidx.annotation.Keep

@Keep
data class HajjpackageRegPayload(
    val Name: String,
    val DateofBirth: String,
    val Gender: String,
    val DocType: String,
    val DocNumber: String,
    val PermanentDistrict: String,
    val PermanentAddress: String,
    val PresentAddress: String,
    val PhoneNumber: String,
    val Language: String,
    val Email: String,
    val maritalStatus: String,
    val MaritalRefName: String
)