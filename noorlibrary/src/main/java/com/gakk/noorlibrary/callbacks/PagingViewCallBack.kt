package com.gakk.noorlibrary.callbacks

import java.io.Serializable

interface PagingViewCallBack : Serializable {
    fun initPagingProperties()
    fun loadNextPage()
    fun hasMoreData(): Boolean
}