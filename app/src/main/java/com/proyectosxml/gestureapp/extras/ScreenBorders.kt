package com.proyectosxml.gestureapp.extras

import android.graphics.Matrix
import android.graphics.RectF
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class ScreenBounds(private val parentView: View, private val bottomBar: View) {

    fun isInBounds(
        newX: Float,
        newY: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float,
        viewImageView: ImageView
    ): Boolean {
        // Obtener las dimensiones del contenedor
        val containerWidth = viewImageView.width
        val containerHeight = viewImageView.height

        // Calcular las coordenadas de los cuatro vértices de la imagen después de la rotación
        val rect = RectF(0f, 0f, viewImageView.width.toFloat(), viewImageView.height.toFloat())
        val matrix = Matrix()
        matrix.postRotate(rotation, rect.centerX(), rect.centerY())
        matrix.postScale(scaleX, scaleY, rect.centerX(), rect.centerY())
        matrix.mapRect(rect)

        // Calcular las coordenadas de los cuatro vértices de la imagen después de la translación
        val vertices = floatArrayOf(
            rect.left + newX,
            rect.top + newY,
            rect.right + newX,
            rect.top + newY,
            rect.right + newX,
            rect.bottom + newY,
            rect.left + newX,
            rect.bottom + newY
        )

        // Verificar si alguno de los vértices está fuera de los límites del contenedor
        for (i in vertices.indices step 2) {
            val x = vertices[i]
            val y = vertices[i + 1]
            if (x < 0 || x > containerWidth || y < 0 || y > containerHeight) {
                return false
            }
        }

        return true
    }
}
