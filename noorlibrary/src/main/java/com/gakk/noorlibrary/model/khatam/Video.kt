package com.gakk.noorlibrary.model.khatam

import java.io.Serializable


data class Videos(val videos: List<KhatamQuranVideosResponse.Data>, val playWhenReady: Boolean = true) : Serializable {
}