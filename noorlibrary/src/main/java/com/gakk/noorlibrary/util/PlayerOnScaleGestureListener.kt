package com.gakk.noorlibrary.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.ScaleGestureDetector
import android.view.View
import com.google.android.exoplayer2.ui.PlayerView
import com.gakk.noorlibrary.R

class PlayerOnScaleGestureListener(
    private val playerView: PlayerView,
    val context:Context,
    ): ScaleGestureDetector.SimpleOnScaleGestureListener() {


    private var contentView:View = playerView.findViewById(R.id.exo_content_frame)
    private var contentHeight = 0
    private var screenHeight = 0
    private var contentWidth = 0
    private var screenWidth = 0
    private var minScale = contentView.scaleX
    private var maxScale = 2f
    private var scaleFactor = minScale
    private val extraScale = 0.2f
    private var midRange = 1.5f
    override fun onScale(
        detector: ScaleGestureDetector
    ): Boolean {

        contentView.measuredWidth

        contentHeight = contentView.measuredHeight
        screenHeight = playerView.measuredHeight

        screenWidth = playerView.measuredWidth
        contentWidth = contentView.measuredWidth

        if(screenWidth == contentWidth){
            maxScale = screenHeight.toFloat() / contentHeight.toFloat()
        }else if(screenHeight == contentHeight){
            maxScale = screenWidth.toFloat() / contentWidth.toFloat()
        }



        scaleFactor *= detector.scaleFactor
        val scaleRange = (minScale - extraScale)..(maxScale + extraScale)
        midRange = ((scaleRange.endInclusive - scaleRange.start) / 2) + scaleRange.start
        scaleFactor = scaleFactor.coerceIn(scaleRange)


        contentView.scaleX = scaleFactor
        contentView.scaleY = scaleFactor
        return true
    }

    override fun onScaleBegin(
        detector: ScaleGestureDetector
    ) : Boolean{
        return true
    }
    override fun onScaleEnd(detector: ScaleGestureDetector) {

        if(scaleFactor>midRange){
            resizeFixedHeightOrWidth()
        }else{
            resizeFit()
        }
    }

    private fun resizeFixedHeightOrWidth() {
        resizeAnimation(scaleFactor,maxScale)
    }

    private fun resizeFit() {
        resizeAnimation(scaleFactor,minScale)
    }
    private fun resizeAnimation(fromScale:Float,toScale:Float){
        ValueAnimator.ofFloat(fromScale, toScale).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                contentView.scaleX  = value
                contentView.scaleY = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    scaleFactor = toScale
                }
            })

        }.start()
    }
}