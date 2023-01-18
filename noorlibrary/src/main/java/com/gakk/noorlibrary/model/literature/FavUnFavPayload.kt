package com.gakk.noorlibrary.model.literature

import androidx.annotation.Keep

/**
 * Used parameter when Calling Favourite/Unfavourite Literature
 */

@Keep
data class FavUnFavPayload(
    val category: String,
    val content: String,
    val subcategory: String
)