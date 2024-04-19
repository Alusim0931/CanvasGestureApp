package com.proyectosxml.gestureapp.gestures

import android.view.MotionEvent
import android.widget.ImageView
import com.proyectosxml.gestureapp.extras.ScreenBounds
import com.proyectosxml.gestureapp.dataclass.RotateGestureState
import kotlin.math.abs
import kotlin.math.atan2

class RotationGestureDetector(listener: OnRotationGestureListener?) {
    private var fX = 0f
    private var fY = 0f
    private var sX = 0f
    private var sY = 0f
    private var ptrID1: Int
    private var ptrID2: Int
    private var mAngle = 0f
    private val mListener: OnRotationGestureListener?

    fun getAngle(): Float {
        return mAngle
    }

    init {
        mListener = listener
        ptrID1 = INVALID_POINTER_ID
        ptrID2 = INVALID_POINTER_ID
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> ptrID1 = event.getPointerId(event.actionIndex)
            MotionEvent.ACTION_POINTER_DOWN -> {
                ptrID2 = event.getPointerId(event.actionIndex)
                sX = event.getX(event.findPointerIndex(ptrID1))
                sY = event.getY(event.findPointerIndex(ptrID1))
                fX = event.getX(event.findPointerIndex(ptrID2))
                fY = event.getY(event.findPointerIndex(ptrID2))
            }

            MotionEvent.ACTION_MOVE -> if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                try {
                    val nsX: Float = event.getX(event.findPointerIndex(ptrID1))
                    val nsY: Float = event.getY(event.findPointerIndex(ptrID1))
                    val nfX: Float = event.getX(event.findPointerIndex(ptrID2))
                    val nfY: Float = event.getY(event.findPointerIndex(ptrID2))
                    mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY)
                    mListener?.onRotation(this)
                }catch (e:Exception){

                }
            }

            MotionEvent.ACTION_UP -> ptrID1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> ptrID2 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                ptrID1 = INVALID_POINTER_ID
                ptrID2 = INVALID_POINTER_ID
            }
        }
        return true
    }

    private fun angleBetweenLines(
        fX: Float,
        fY: Float,
        sX: Float,
        sY: Float,
        nfX: Float,
        nfY: Float,
        nsX: Float,
        nsY: Float
    ): Float {
        val angle1 = atan2((fY - sY).toDouble(), (fX - sX).toDouble()).toFloat()
        val angle2 =
            atan2((nfY - nsY).toDouble(), (nfX - nsX).toDouble()).toFloat()
        var angle = Math.toDegrees((angle2 - angle1).toDouble()).toFloat() % 360
        if (angle < 0) angle += 360
        return angle
    }

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector?)
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}

    interface OnRotateGestureListener {
        fun onRotate(rotation: Float, imageView: ImageView) {
            val pivotX = imageView.width / 2f
            val pivotY = imageView.height / 2f

            imageView.pivotX = pivotX
            imageView.pivotY = pivotY

            imageView.rotation += rotation
        }
    }