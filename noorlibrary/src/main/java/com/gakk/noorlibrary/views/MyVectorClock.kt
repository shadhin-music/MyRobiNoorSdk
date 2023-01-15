package com.gakk.noorlibrary.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import com.gakk.noorlibrary.R


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 5/3/2021, Mon
 */
class MyVectorClock : VectorClock {

    private fun init() {
        //use this for the default Analog Clock (recommended)
        //initializeSimple()

        initializeCustom(R.drawable.ic_clc_face_id, R.drawable.hours_hand,
            R.drawable.minutes_hand, R.drawable.second_hand)

        //or use this if you want to use your own vector assets (not recommended)
        //initializeCustom(faceResourceId, hourResourceId, minuteResourceId, secondResourceId);
    }

    //mandatory constructor
    constructor(ctx: Context) : super(ctx) {
        init()
    }

    // the other constructors are in case you want to add the view in XML
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }
}