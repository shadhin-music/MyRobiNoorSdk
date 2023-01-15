package com.gakk.noorlibrary.callbacks

import java.io.Serializable

interface PagingViewCallBack : Serializable {

    //initialise pageNo=1 & hasMoreData=true
    fun initPagingProperties()
    fun loadNextPage()
    fun hasMoreData(): Boolean
}