package com.proyectosxml.gestureapp

import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import kotlin.math.atan2

/*class RotateGestureDetector(private val context: ImageView, private val mListener: OnRotateGestureListener) {
    private var mPrevSlope = 0f
    private var mCurrSlope = 0f

    interface OnRotateGestureListener {
        fun onRotate(angle: Float)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val imageBounds = IntArray(2)
        context.getLocationOnScreen(imageBounds)

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                val xInView = event.getX(0)
                val yInView = event.getY(0)
                if (isPointInsideView(xInView, yInView, context)) {
                    mCurrSlope = calculateSlope(event)
                    if (mPrevSlope != 0f) {
                        mListener.onRotate(mCurrSlope - mPrevSlope)
                    }
                    mPrevSlope = mCurrSlope
                }
            }
            MotionEvent.ACTION_POINTER_UP -> mPrevSlope = 0f
        }
        return true
    }

    private fun calculateSlope(event: MotionEvent): Float {
        if (event.pointerCount > 1) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return atan2(y, x)
        }
        return 0f
    }

    private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]
        val viewWidth = view.width
        val viewHeight = view.height
        return !(x < viewX || x > viewX + viewWidth || y < viewY || y > viewY + viewHeight)
    }
}*/