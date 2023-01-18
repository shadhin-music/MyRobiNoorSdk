package com.gakk.noorlibrary.model.subcategory

import androidx.annotation.Keep

@Keep
data class SubcategoriesByCategoryIdResponse(
    val `data`: MutableList<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)