package com.proyectosxml.gestureapp

import android.content.Context
import android.view.MotionEvent
import kotlin.math.atan2

class RotateGestureDetector(context: Context, private val mListener: OnRotateGestureListener) {
    private var mPrevSlope = 0f
    private var mCurrSlope = 0f

    interface OnRotateGestureListener {
        fun onRotate(angle: Float)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> {
                mCurrSlope = calculateSlope(event)
                if (mPrevSlope != 0f) {
                    mListener.onRotate(mCurrSlope - mPrevSlope)
                }
                mPrevSlope = mCurrSlope
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
}