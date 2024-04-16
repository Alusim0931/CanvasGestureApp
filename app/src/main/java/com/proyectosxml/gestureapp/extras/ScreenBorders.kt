package com.proyectosxml.gestureapp.extras

import android.view.View

class ScreenBounds(private val parentView: View, private val bottomBar: View) {

    fun isInsideBounds(view: View, newX: Float, newY: Float): Boolean {
        val parentWidth = parentView.width
        val parentHeight = parentView.height - bottomBar.height // Subtract the height from the bottomBar

        // Calculate the boundaries of the area within the parentView
        val leftBound = 0f
        val topBound = 0f
        val rightBound = parentWidth.toFloat()
        val bottomBound = parentHeight.toFloat()

        // Calculate the scaled width and height of the view
        val scaledWidth = view.width * view.scaleX
        val scaledHeight = view.height * view.scaleY

        // Calculate the adjusted left and top bounds taking into account the scaling
        val adjustedLeftBound = leftBound + (view.left - leftBound) * view.scaleX
        val adjustedTopBound = topBound + (view.top - topBound) * view.scaleY

        // Calculate the adjusted right and bottom bounds taking into account the scaling
        val adjustedRightBound = rightBound + (scaledWidth - view.width) / 2
        val adjustedBottomBound = bottomBound + (scaledHeight - view.height) / 2

        // Check if the new position is within limits
        return newX >= adjustedLeftBound && newX + scaledWidth <= adjustedRightBound &&
                newY >= adjustedTopBound && newY + scaledHeight <= adjustedBottomBound
    }
}
