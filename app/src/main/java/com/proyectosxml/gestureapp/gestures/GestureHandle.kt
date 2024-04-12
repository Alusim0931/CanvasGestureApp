package com.proyectosxml.gestureapp.gestures

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import com.proyectosxml.gestureapp.R
import com.proyectosxml.gestureapp.extras.ScreenBounds
import com.proyectosxml.gestureapp.dataclass.GestureState
import com.proyectosxml.gestureapp.dataclass.ImageState
import com.proyectosxml.gestureapp.main.MainActivity
import com.proyectosxml.gestureapp.main.MainCanvasScreen

class GestureHandler(
    private val activity: MainActivity,
    private val imageView: ImageView,
    private val mainCanvasScreen: MainCanvasScreen,
    private val imageState: ImageState
) {
    private val mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(
        activity,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (scaleFactor > 1 || imageView.scaleX * scaleFactor > 1) {
                    imageView.scaleX *= scaleFactor
                    imageView.scaleY *= scaleFactor
                    mainCanvasScreen.setImageScaleX(imageView.scaleX)
                    mainCanvasScreen.setImageScaleY(imageView.scaleY)
                }
                return true
            }
        })

    private val screenBounds: ScreenBounds = ScreenBounds(activity.findViewById(R.id.frameLayout), activity.findViewById(
        R.id.bottomAppBar
    ))

    private val rotateGestureDetector: RotateGestureDetector = RotateGestureDetector(
        object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(rotation: Float, imageView: ImageView) {
                if (imageView.x >= 0 && imageView.y >= 0 && imageView.x + imageView.width <= imageView.rootView.width && imageView.y + imageView.height <= imageView.rootView.height) {
                    imageView.rotation += rotation
                    mainCanvasScreen.setImageRotation(imageView.rotation)
                }
            }
        }, imageView, screenBounds
    )

    fun handleTouchEvent(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                imageState.currentGesture = GestureState.MOVE
                imageState.savedImageX = event.rawX - view.x
                imageState.savedImageY = event.rawY - view.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!imageState.isGestureInProgress) {
                    imageState.isGestureInProgress = true
                    imageState.currentGesture =
                        if (event.pointerCount == 2) GestureState.SCALE_AND_ROTATE else GestureState.NONE
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (imageState.currentGesture) {
                    GestureState.MOVE -> {
                        val dx = event.rawX - imageState.savedImageX
                        val dy = event.rawY - imageState.savedImageY
                        if (screenBounds.isInsideBounds(view, dx, dy)) {
                            view.x = dx
                            view.y = dy
                            imageState.finalImageX = dx
                            imageState.finalImageY = dy
                        }
                    }
                    GestureState.SCALE_AND_ROTATE -> {
                        mScaleGestureDetector.onTouchEvent(event)
                        rotateGestureDetector.onTouchEvent(event)
                    }
                    else -> {}
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    imageState.currentGesture = GestureState.MOVE
                    imageState.isGestureInProgress = false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                imageState.currentGesture = GestureState.NONE
                imageState.isGestureInProgress = false
            }
        }
        return true
    }

    fun setupImageTouchListener(view: ImageView) {
        // TODO: Implement the functionality of this method
    }
}