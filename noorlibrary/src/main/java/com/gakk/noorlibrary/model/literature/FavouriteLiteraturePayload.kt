package com.gakk.noorlibrary.model.literature

import androidx.annotation.Keep

/**
 * Used as parameter when requesting for Favourite Literature List
 */

@Keep
data class FavouriteLiteraturePayload(
    val category: String,
    val subcategory: String
)