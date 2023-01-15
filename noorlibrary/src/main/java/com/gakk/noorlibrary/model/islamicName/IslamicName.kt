package com.gakk.noorlibrary.model.islamicName

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/18/2021, Sun
 */
data class IslamicName(
    val name: String,
    val meaning: String,
    val gender: String,
    val language: String,
    val id: String,
    var userFavouritedThis: Boolean,
    val order: Int
)
