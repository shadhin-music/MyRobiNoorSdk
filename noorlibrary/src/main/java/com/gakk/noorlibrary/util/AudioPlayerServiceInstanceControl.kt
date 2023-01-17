package com.gakk.noorlibrary.util

import android.content.Context
import android.content.Intent
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.service.AudioPlayerService
import java.io.Serializable

object AudioPlayerServiceInstanceControl {

    fun startService(context: Context?,type:String,index:Int?=null,surahList:MutableList<Data>){
       Intent(context,AudioPlayerService::class.java).also {
            when(type){
                SURAH_LIST_TYPE->{
                    it.putExtra(PLAY_LIST_TYPE,type)
                    it.putExtra(CURRENT_INDEX,index)
                    it.putExtra(PLAY_LIST,surahList as Serializable)
                    context?.startService(it)
                }
            }

        }
    }

    fun stopService(context: Context){
        Intent(context,AudioPlayerService::class.java).also {
            context.stopService(it)

        }
    }
}