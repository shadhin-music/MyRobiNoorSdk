package com.gakk.noorlibrary.model.video.category

import androidx.annotation.Keep

@Keep
data class VideoByGroup(
    val catId:String?=null,
    val contentList:MutableList<Data>
)