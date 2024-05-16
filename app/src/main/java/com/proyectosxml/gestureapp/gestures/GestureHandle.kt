package com.proyectosxml.gestureapp.gestures

import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import com.proyectosxml.gestureapp.dataclass.GestureState
import com.proyectosxml.gestureapp.dataclass.ImageState
import com.proyectosxml.gestureapp.main.MainActivity

class GestureHandler(
    private val activity: MainActivity,
    private val imageView: ImageView,
    private val imageState: ImageState
) {
    private val mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(
        activity,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                val scaleX = (scaleFactor * imageView.scaleX).coerceIn(0.3f, 4f)
                val scaleY = (scaleFactor * imageView.scaleY).coerceIn(0.3f, 4f)

                if (isInBounds(
                        imageView.x,
                        imageView.y,
                        imageView.rotation,
                        scaleX,
                        scaleY,
                        imageView
                    )
                ) {
                    imageView.scaleX = scaleX
                    imageView.scaleY = scaleY
                }
                return true
            }
        })

    fun handleTouchEvent(view: View, event: MotionEvent): Boolean {
        Log.d("GestureHandler", "handleTouchEvent called with event: $event")
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                imageState.currentGesture = GestureState.MOVE
                imageState.savedImageX = event.rawX - view.x
                imageState.savedImageY = event.rawY - view.y
            }

            MotionEvent.ACTION_MOVE -> {
                when (imageState.currentGesture) {
                    GestureState.MOVE -> {
                        val dx = event.rawX - imageState.savedImageX
                        val dy = event.rawY - imageState.savedImageY
                        imageView.x = dx
                        imageView.y = dy
                        imageState.finalImageX = dx
                        imageState.finalImageY = dy
                    }

                    GestureState.SCALE_AND_ROTATE -> {
                        mScaleGestureDetector.onTouchEvent(event)
                    }

                    else -> {}
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                imageState.currentGesture = GestureState.NONE
            }
        }
        return true
    }

    private fun isInBounds(
        x: Float,
        y: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float,
        imageFrame: Any
    ): Boolean {
        // Implementar la lógica para verificar si la imagen está dentro de los límites
        return true
    }

    fun setupImageTouchListener(imageView: ImageView?) {

    }
}