package com.gakk.noorlibrary.model.tracker

import androidx.annotation.Keep

@Keep
data class SalahStatus(
    val fajr: Boolean?=null,
    val zuhr: Boolean?=null,
    val asar: Boolean?=null,
    val maghrib: Boolean?=null,
    val isha: Boolean?=null
)