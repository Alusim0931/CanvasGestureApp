package com.proyectosxml.gestureapp

import android.view.View

class ScreenBounds(private val parentView: View, private val bottomBar: View) {

    fun isInsideBounds(view: View, newX: Float, newY: Float): Boolean {
        val viewWidth = view.width
        val viewHeight = view.height
        val parentWidth = parentView.width
        val parentHeight = parentView.height - bottomBar.height // Restar la altura de la bottomBar

        // Calcular los límites del área dentro del parentView
        val leftBound = 0f
        val topBound = 0f
        val rightBound = (parentWidth - viewWidth).toFloat()
        val bottomBound = (parentHeight - viewHeight).toFloat()

        // Verificar si la nueva posición está dentro de los límites
        return newX >= leftBound && newX <= rightBound && newY >= topBound && newY <= bottomBound
    }
}
