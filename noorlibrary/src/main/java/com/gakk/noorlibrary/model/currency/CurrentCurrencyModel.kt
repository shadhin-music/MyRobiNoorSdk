package com.gakk.noorlibrary.model.currency

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/28/2021, Wed
 */
data class CurrentCurrencyModel(
    val isSuccess: Boolean,
    val timeStamp: Int,
    val date: String,
    val from: String,
    val to: String
)
