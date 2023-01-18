package com.gakk.noorlibrary.model.khatam

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class Videos(val videos: List<KhatamQuranVideosResponse.Data>, val playWhenReady: Boolean = true) : Serializable {
}