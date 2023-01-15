package com.gakk.noorlibrary.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewNormalArabic(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {

    init {
            this.typeface =
                Typeface.createFromAsset(context.assets, "Al_Majeed_Quranic_Font_shiped.ttf")
    }
}