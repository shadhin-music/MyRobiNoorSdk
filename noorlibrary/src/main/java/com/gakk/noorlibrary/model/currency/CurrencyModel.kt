package com.gakk.noorlibrary.model.currency

import com.gakk.noorlibrary.util.FLAG_BASE_URL
import java.util.*

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/28/2021, Wed
 */
data class CurrencyModel(
    val alphabeticCode: String,
    val currency: String,
    val entity: String,
    val minorUnit: String,
    val numericCode: String,
    val countryCode: String
) {
    val fullImageUrl: String
        get() = "$FLAG_BASE_URL/${countryCode.lowercase(Locale.ENGLISH)}.png"
}
