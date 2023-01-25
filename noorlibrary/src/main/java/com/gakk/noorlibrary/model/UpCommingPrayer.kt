package com.gakk.noorlibrary.model

import androidx.annotation.Keep

@Keep
data class UpCommingPrayer(

    var currentWaqtStartingTime: String,

    var nextWaqtName: String,


    var currentWaqtName: String,

    var nextWaqtNameTracker: String,


    var nextWaqtTime: String,


    var timeLeft: String
)