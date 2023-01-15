package com.gakk.noorlibrary.model.literature

/**
 * Used parameter when Calling Favourite/Unfavourite Literature
 */
data class FavUnFavPayload(
    val category: String,
    val content: String,
    val subcategory: String
)