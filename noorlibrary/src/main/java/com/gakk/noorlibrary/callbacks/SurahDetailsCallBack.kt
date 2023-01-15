package com.gakk.noorlibrary.callbacks

import java.io.Serializable

interface SurahDetailsCallBack: Serializable {
    fun updateSelection(id:String)
}