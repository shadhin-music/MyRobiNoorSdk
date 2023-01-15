package com.gakk.noorlibrary.model.literature

/**
 * Used as parameter when requesting for Favourite Literature List
 */
data class FavouriteLiteraturePayload(
    val category: String,
    val subcategory: String
)