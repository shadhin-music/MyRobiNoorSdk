package com.gakk.noorlibrary.util

import com.gakk.noorlibrary.callbacks.VideoPlayPauseListener

object VideoPlayPauseObserver {
    private var listener:VideoPlayPauseListener?=null

    fun setPlayPauseListener(listener:VideoPlayPauseListener){
        this.listener=listener
    }
    fun removePlayPauseListener(){
        this.listener=null
    }

    fun notifyPlayPauseListener(){
        listener?.notifyItem()
    }
}