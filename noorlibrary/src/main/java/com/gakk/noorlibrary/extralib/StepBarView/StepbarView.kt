package com.gakk.noorlibrary.extralib.StepBarView

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class stepbarView() : SlideStepAdapter.slider_step_item_clickListner {

    private lateinit var step_adapter : SlideStepAdapter
    private var clickable:Boolean = false
    private lateinit var viewpager:ViewPager
    private lateinit var activeStepCallback: stepListner


    fun setup_stepbar(
        mContext: Context,
        stepview:RecyclerView,
        totalStep:Int,
        stepRadius:Float,
        stepHeight:Float,
        stepTextSize:Float,
        isClickable:Boolean,
        get_viewpager: ViewPager?,
        enableTitle:Boolean,


        )
    {
        val stepView = stepview
        step_adapter = SlideStepAdapter(
            mContext,
            arrayOfNulls<Int>(totalStep),
            0,
            stepRadius,
            stepHeight,
            stepTextSize,
            enableTitle)

        val linearLayoutManager = object : LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false) {
            override fun canScrollHorizontally(): Boolean {
                return !enableTitle
            }
        }

        stepView.layoutManager = linearLayoutManager
        step_adapter.setOnItemClick(this)
        stepView.setAdapter(step_adapter)
        clickable = isClickable

        if (get_viewpager != null) {
            this.viewpager = get_viewpager

            viewpager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    // Check if this is the page you want.
                    Log.e("ViewPageger with step: ", position.toString())
                    step_adapter.update_active_step(position)
                    step_adapter.notifyDataSetChanged()
                }
            })
        }



    }

    interface stepListner
    {
        fun setOnActiveStep(step: Int)
    }

    fun getActiveStep(activeStepCallback: stepListner?) {
        if (activeStepCallback != null) {
            this.activeStepCallback = activeStepCallback
        }
    }

    fun setActiveStep(step:Int)
    {
        step_adapter.update_active_step(step)
        step_adapter.notifyDataSetChanged()

    }


    override fun slider_step_clicked(step: Int) {
        if(clickable) {
            activeStepCallback.setOnActiveStep(step)
            //step_adapter.update_active_step(step)
            //step_adapter.notifyDataSetChanged()

        }

    }


}