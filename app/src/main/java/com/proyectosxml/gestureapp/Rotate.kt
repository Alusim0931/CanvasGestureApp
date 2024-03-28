import android.view.MotionEvent
import kotlin.math.atan2

class RotateGestureDetector(private val listener: OnRotateGestureListener) {
    private var prevAngle = 0.0

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 2) {
            val deltaX = (event.getX(0) - event.getX(1)).toDouble()
            val deltaY = (event.getY(0) - event.getY(1)).toDouble()
            val angle = Math.toDegrees(atan2(deltaY, deltaX))

            if (prevAngle != 0.0) {
                val rotation = angle - prevAngle
                listener.onRotate(rotation.toFloat())
            }

            prevAngle = angle
        } else {
            prevAngle = 0.0
        }

        return true
    }

    interface OnRotateGestureListener {
        fun onRotate(rotation: Float)
    }
}