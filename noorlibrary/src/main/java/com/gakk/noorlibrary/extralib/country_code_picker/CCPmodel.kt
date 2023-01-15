package com.gakk.noorlibrary.extralib.country_code_picker


import com.gakk.noorlibrary.util.FLAG_BASE_URL
import java.util.*

data class CCPmodel(
    var countryCode: String,
    var countryName: String,
    var dialCode: String?
)
{
    val fullImageUrl: String
        get() = "$FLAG_BASE_URL/${countryCode.lowercase(Locale.ENGLISH)}.png"
}