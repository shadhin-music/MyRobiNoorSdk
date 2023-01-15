package com.gakk.noorlibrary.model.subcategory

data class SubcategoriesByCategoryIdResponse(
    val `data`: MutableList<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)