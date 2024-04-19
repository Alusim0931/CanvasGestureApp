package com.proyectosxml.gestureapp.extras

import android.graphics.Matrix
import android.graphics.RectF
import android.view.View

class ScreenBounds(private val parentView: View, private val bottomBar: View) {

    fun isInBounds(
        dx: Float,
        dy: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float
    ): Boolean {
        val containerWidth = parentView.width
        val containerHeight = parentView.height - bottomBar.height

        // Calculate the coordinates of the four vertices of the image after rotation and scale
        val rect = RectF(0f, 0f, parentView.width.toFloat(), parentView.height.toFloat())
        val matrix = Matrix()
        matrix.postRotate(rotation, rect.centerX(), rect.centerY())
        matrix.postScale(scaleX, scaleY, rect.centerX(), rect.centerY())
        matrix.mapRect(rect)

        // Adjust the rectangle by the translation (dx, dy)
        rect.offset(dx, dy)

        // Check if any part of the image is outside the container bounds
        return rect.left >= 0 && rect.top >= 0 &&
                rect.right <= containerWidth && rect.bottom <= containerHeight
    }
}