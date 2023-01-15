package com.gakk.noorlibrary.model.tracker

data class SalahStatus(
    val fajr: Boolean?=null,
    val zuhr: Boolean?=null,
    val asar: Boolean?=null,
    val maghrib: Boolean?=null,
    val isha: Boolean?=null
)