package com.gakk.noorlibrary.data.wrapper

import com.gakk.noorlibrary.model.literature.Literature
import java.io.Serializable

data class LiteratureListWrapper(val literatures: MutableList<Literature>?):Serializable
