package com.gakk.noorlibrary.model.hajjpackage

import androidx.annotation.Keep
import java.io.File

@Keep
data class PersonalInfoItem(
    val file: File?,
    val name: String,
    val dateofBirth: String,
    val gender: String,
    val docType: String,
    val docNumber: String,
    val maritalStatus: String,
    val maritalRefName: String
)
