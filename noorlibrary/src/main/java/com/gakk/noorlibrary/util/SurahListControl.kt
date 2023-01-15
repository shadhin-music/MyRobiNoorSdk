package com.gakk.noorlibrary.util

import com.gakk.noorlibrary.model.quran.surah.Data

object SurahListControl {

    var surahList: MutableList<Data>? = null
    var curIndex: Int? = null

    fun incrementCurrentIndex() {
        if (curIndex!! < surahList!!.size - 1)
            curIndex = curIndex!! + 1
    }

    fun decrementCurrentIndex() {
        if (curIndex!! > 0)
            curIndex = curIndex!! - 1
    }

    fun copySurahList(list: MutableList<Data>?) {
        surahList = list?.mutableCopyOf()
    }

    fun updateSelectedIndex(mSurahId: String) {
        curIndex = -1
        surahList?.let {
            for (i in it.indices) {
                if (it[i].id == mSurahId) {
                    curIndex = i
                    break
                }
            }
        }
    }

    fun getSurahIndexById(id: String): Int {
        surahList?.let {
            for (i in it.indices) {
                if (it[i].id == id) {
                    return i
                }
            }
        }
        return -1
    }
        fun getSelectedSurah() = surahList!!.get(curIndex!!)
        fun getSelectedSurahId(): String {
            var index= curIndex?:0
            if(index> surahList?.size!!-1){
                index=surahList?.size!!-1
            }
          return  surahList!!.get(index).id!!
        }
    }