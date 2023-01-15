package com.gakk.noorlibrary.callbacks

import com.gakk.noorlibrary.model.literature.Literature
import java.io.Serializable

interface LiteratureListCallBack:LiteratureListCallBackParent,Serializable
interface LiteratureListCallBackParent {
    fun getLiteratureList():MutableList<Literature>?
}
