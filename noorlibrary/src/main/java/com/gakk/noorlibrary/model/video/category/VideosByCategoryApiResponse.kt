package com.gakk.noorlibrary.model.video.category

data class VideosByCategoryApiResponse(
    val `data`: MutableList<Data>,
    val error: Any,
    val message: String,
    val status: Int,
    val totalRecords: Int
)