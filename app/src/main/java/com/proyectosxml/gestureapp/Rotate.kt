package com.proyectosxml.gestureapp

import android.view.MotionEvent
import android.widget.ImageView
import com.proyectosxml.gestureapp.dataclass.RotateGestureState
import kotlin.math.abs
import kotlin.math.atan2

class RotateGestureDetector(
    private val listener: OnRotateGestureListener,
    private val imageView: ImageView,
    private val screenBounds: ScreenBounds
) {
    private var rotateGestureState = RotateGestureState()

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount != 2) {
            return false
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                val x1 = event.getX(0)
                val y1 = event.getY(0)
                val x2 = event.getX(1)
                val y2 = event.getY(1)

                // Check if both fingers are within the image range
                if (isWithinBounds(x1, y1) && isWithinBounds(x2, y2)) {
                    rotateGestureState.prevX1 = x1
                    rotateGestureState.prevY1 = y1
                    rotateGestureState.prevX2 = x2
                    rotateGestureState.prevY2 = y2
                    rotateGestureState.initialAngle = Math.toDegrees(atan2(y2 - y1, x2 - x1).toDouble())
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val x1 = event.getX(0)
                val y1 = event.getY(0)
                val x2 = event.getX(1)
                val y2 = event.getY(1)

                // Check if both fingers are within the image range
                if (isWithinBounds(x1, y1) && isWithinBounds(x2, y2)) {
                    val deltaX1 = x1 - rotateGestureState.prevX1
                    val deltaY1 = y1 - rotateGestureState.prevY1
                    val deltaX2 = x2 - rotateGestureState.prevX2
                    val deltaY2 = y2 - rotateGestureState.prevY2

                    // Only processes rotation if both fingers move
                    if (deltaX1 != 0f || deltaY1 != 0f || deltaX2 != 0f || deltaY2 != 0f) {
                        // Calculate the total travel distance
                        val distanceMoved = Math.sqrt(
                            (deltaX1 * deltaX1 + deltaY1 * deltaY1 + deltaX2 * deltaX2 + deltaY2 * deltaY2).toDouble()
                        )

                        // Calculate the current angle
                        val angle = Math.toDegrees(atan2(y2 - y1, x2 - x1).toDouble())

                        // Calculates the difference in angles between the current angle and the initial angle
                        val angleDifference = angle - rotateGestureState.initialAngle

                        // If the displacement distance exceeds the threshold and the direction is significant,
                        // We consider that it is a rotation gesture
                        if (distanceMoved > rotateGestureState.rotationThreshold && abs(angleDifference) > rotateGestureState.rotationThreshold) {
                            rotateGestureState.accumulatedAngle += angleDifference.toFloat() * rotateGestureState.rotationScaleFactor // Ralentiza la rotación
                            listener.onRotate(rotateGestureState.accumulatedAngle, imageView)
                        }
                    }

                    rotateGestureState.prevX1 = x1
                    rotateGestureState.prevY1 = y1
                    rotateGestureState.prevX2 = x2
                    rotateGestureState.prevY2 = y2
                }
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                rotateGestureState.prevX1 = 0f
                rotateGestureState.prevY1 = 0f
                rotateGestureState.prevX2 = 0f
                rotateGestureState.prevY2 = 0f
                rotateGestureState.initialAngle = 0.0
            }
        }
        return true
    }

    // Check if the given coordinates are within the image range
    private fun isWithinBounds(x: Float, y: Float): Boolean {
        return x >= 0 && x <= imageView.width && y >= 0 && y <= imageView.height
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
}