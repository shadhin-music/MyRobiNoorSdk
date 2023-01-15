package com.gakk.noorlibrary.model.tracker

data class Data(
    val about: Any,
    val createdBy: String,
    val createdOn: String,
    val id: String,
    val isActive: Boolean,
    val language: String,
    val order: Int,
    var salahStatus: SalahStatus,
    val updatedBy: Any,
    val updatedOn: Any
)