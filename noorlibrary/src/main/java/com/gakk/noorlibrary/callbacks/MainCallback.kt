package com.gakk.noorlibrary.callbacks

import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.quran.surah.Data

interface MainCallback  {
    fun openSuraDetailsById(id: String)
    fun openDetailsActivityWithPageName(
        pageName: String,
        surahId: String? = null,
        surahList: MutableList<Data>? = null,
        selectedIndex: Int? = null,
        literatureListCallBack: LiteratureListCallBack? = null,
        currentPageNo: Int? = null,
        isFav: Boolean? = null,
        catId: String? = null,
        subCatId: String? = null,
        pageTitle: String? = null,
        itemCount: Int? = null,
        times: Int? = null,
        literatures: MutableList<Literature>? = null,
        isFromHomeEvent: Boolean? = null
    )

    fun getWindowHeight(): Int
    fun getScreenWith(): Int
    fun showToastMessage(message: String)
    fun makeMoreFragmentNull()
    fun openCurrentSurahById(id: String)
}