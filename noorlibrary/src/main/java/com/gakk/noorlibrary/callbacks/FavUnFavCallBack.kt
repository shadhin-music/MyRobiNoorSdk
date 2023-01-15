package com.gakk.noorlibrary.callbacks

interface FavUnFavCallBack {
    fun performFavOrUnFavAction(catId:String, subCatId:String, literatureId:String, indexInAdapter:Int?=-1, makeFav:Boolean)
}