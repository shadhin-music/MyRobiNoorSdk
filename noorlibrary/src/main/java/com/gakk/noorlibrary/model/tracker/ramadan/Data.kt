package com.gakk.noorlibrary.model.tracker.ramadan

data class Data(
    val about: Any,
    val createdBy: String,
    val createdOn: String,
    val id: String,
    val isActive: Boolean,
    val language: String,
    val order: Int,
    val ramadanStatus: Boolean,
    val updatedBy: String,
    val updatedOn: String
)