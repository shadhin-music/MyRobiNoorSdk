package com.gakk.noorlibrary.model.currency

import androidx.annotation.Keep

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/28/2021, Wed
 */

@Keep
data class CurrentCurrencyModel(
    val isSuccess: Boolean,
    val timeStamp: Int,
    val date: String,
    val from: String,
    val to: String
)
