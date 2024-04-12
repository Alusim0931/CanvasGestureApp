package com.proyectosxml.gestureapp.extras

import android.view.View

class ScreenBounds(private val parentView: View, private val bottomBar: View) {

    fun isInsideBounds(view: View, newX: Float, newY: Float): Boolean {
        val viewWidth = view.width
        val viewHeight = view.height
        val parentWidth = parentView.width
        // Subtract the height from the bottomBar
        val parentHeight = parentView.height - bottomBar.height

        // Calculate the boundaries of the area within the parentView
        val leftBound = 0f
        val topBound = 0f
        val rightBound = (parentWidth - viewWidth).toFloat()
        val bottomBound = (parentHeight - viewHeight).toFloat()

        // Check if the new position is within limits
        return newX in leftBound..rightBound && newY >= topBound && newY <= bottomBound
    }
}
