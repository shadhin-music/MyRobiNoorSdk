package com.gakk.noorlibrary.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayout


class CustomTabLayout : TabLayout {
    private var mTypeface: Typeface? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        /*mTypeface = Typeface.createFromAsset(
            context.assets,
            "Roboto_regular.ttf"
        )*/
    }

    override fun addTab(tab: Tab) {
        super.addTab(tab)
        val mainView = getChildAt(0) as ViewGroup
        val tabView = mainView.getChildAt(tab.position) as ViewGroup
        val tabChildCount = tabView.childCount
        for (i in 0 until tabChildCount) {
            val tabViewChild = tabView.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.setTypeface(mTypeface, Typeface.NORMAL)
            }
        }
    }
}