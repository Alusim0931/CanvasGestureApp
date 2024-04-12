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

/**
 * Handles touch events and gestures on an ImageView.
 */
class GestureHandler(
    private val activity: MainActivity,
    private val imageView: ImageView,
    private val mainCanvasScreen: MainCanvasScreen,
    private val imageState: ImageState
) {
    // Scale gesture detector for pinch-to-zoom gesture
    private val mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(
        activity,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                // Ensure the scale factor doesn't make the image too small or too large
                if (scaleFactor > 1 || imageView.scaleX * scaleFactor > 1) {
                    imageView.scaleX *= scaleFactor
                    imageView.scaleY *= scaleFactor
                    mainCanvasScreen.setImageScaleX(imageView.scaleX)
                    mainCanvasScreen.setImageScaleY(imageView.scaleY)
                }
                return true
            }
        })

    // Bounds of the screen to restrict gestures within the visible area
    private val screenBounds: ScreenBounds = ScreenBounds(
        activity.findViewById(R.id.frameLayout),
        activity.findViewById(R.id.bottomAppBar)
    )

    // Rotate gesture detector for rotation gesture
    private val rotateGestureDetector: RotateGestureDetector = RotateGestureDetector(
        object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(rotation: Float, imageView: ImageView) {
                // Check if the rotation keeps the ImageView within the visible area
                if (imageView.x >= 0 && imageView.y >= 0 && imageView.x + imageView.width <= imageView.rootView.width && imageView.y + imageView.height <= imageView.rootView.height) {
                    imageView.rotation += rotation
                    mainCanvasScreen.setImageRotation(imageView.rotation)
                }
            }
        }, imageView, screenBounds
    )

    /**
     * Handles touch events on the ImageView.
     */
    fun handleTouchEvent(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Start tracking MOVE gesture
                imageState.currentGesture = GestureState.MOVE
                // Save the initial touch coordinates
                imageState.savedImageX = event.rawX - view.x
                imageState.savedImageY = event.rawY - view.y
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!imageState.isGestureInProgress) {
                    imageState.isGestureInProgress = true
                    // Detect if it's a SCALE_AND_ROTATE or MOVE gesture based on the number of pointers
                    imageState.currentGesture =
                        if (event.pointerCount == 2) GestureState.SCALE_AND_ROTATE else GestureState.NONE
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when (imageState.currentGesture) {
                    GestureState.MOVE -> {
                        // Handle MOVE gesture
                        val dx = event.rawX - imageState.savedImageX
                        val dy = event.rawY - imageState.savedImageY
                        // Ensure the new position keeps the ImageView within the visible area
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
                        imageState.imageScaleX = imageView.scaleX
                        imageState.imageScaleY = imageView.scaleY
                        imageState.imageRotation = imageView.rotation

                        val newWidth = imageView.width * imageState.imageScaleX
                        val newHeight = imageView.height * imageState.imageScaleY
                        if (imageView.x + newWidth > mainCanvasScreen.width) {
                            imageView.x = mainCanvasScreen.width - newWidth
                            imageState.finalImageX = imageView.x
                        }
                        if (imageView.y + newHeight > mainCanvasScreen.height) {
                            imageView.y = mainCanvasScreen.height - newHeight
                            imageState.finalImageY = imageView.y
                        }
                    }
                    else -> {}
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                // End SCALE_AND_ROTATE or MOVE gesture when a pointer is lifted
                if (event.pointerCount <= 1) {
                    imageState.currentGesture = GestureState.MOVE
                    imageState.isGestureInProgress = false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Reset gesture state when touch event ends
                imageState.currentGesture = GestureState.NONE
                imageState.isGestureInProgress = false
            }
        }
        return true
    }

    /**
     * Sets up touch listener for the ImageView.
     */
    fun setupImageTouchListener(view: ImageView) {
    }
}
