package com.gakk.noorlibrary.model.islamicName

import androidx.annotation.Keep

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/18/2021, Sun
 */

@Keep
data class IslamicName(
    val name: String,
    val meaning: String,
    val gender: String,
    val language: String,
    val id: String,
    var userFavouritedThis: Boolean,
    val order: Int
)
