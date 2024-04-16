package com.proyectosxml.gestureapp.gestures

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.view.ScaleGestureDetector
import android.widget.ImageView

class GraphScale(private val context: Context, private val imageView: ImageView) {
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var originalImageWidth = 0
    private var originalImageHeight = 0

    @SuppressLint("ClickableViewAccessibility")
    fun scaleGraph() {
        // Get the original dimensions of the image
        originalImageWidth = imageView.drawable?.intrinsicWidth ?: 0
        originalImageHeight = imageView.drawable?.intrinsicHeight ?: 0

        // Create ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

        // Add an OnTouchListener to ImageView
        imageView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            true // Indicate that the touch event has been consumed
        }
    }

    private inner class ScaleListener :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor

            // Limit the scaling factor to prevent the image from becoming too large or too small
            scaleFactor = maxOf(0.1f, minOf(scaleFactor, 5.0f))

            // Apply scaling to the image
            val matrix = Matrix()
            matrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            imageView.imageMatrix = matrix

            // Adjust the bounds of the screen based on the scale factor
            val screenWidth = imageView.width
            val screenHeight = imageView.height
            val newImageWidth = originalImageWidth * scaleFactor
            val newImageHeight = originalImageHeight * scaleFactor
            val maxTranslateX = (newImageWidth - screenWidth).coerceAtLeast(0f)
            val maxTranslateY = (newImageHeight - screenHeight).coerceAtLeast(0f)
            imageView.translationX = imageView.translationX.coerceIn(-maxTranslateX, 0f)
            imageView.translationY = imageView.translationY.coerceIn(-maxTranslateY, 0f)

            return true
        }
    }
}
