package com.proyectosxml.gestureapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class GraphScaler(private val context: Context, private val graphView: View) {
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    @SuppressLint("ClickableViewAccessibility")
    fun scaleGraph() {
        // Crear un ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

        // Agregar un OnTouchListener al gráfico
        graphView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            true // Indicar que se ha consumido el evento de toque
        }
    }

    private inner class ScaleListener :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor

            // Limitar el factor de escala para evitar que el gráfico se vuelva demasiado grande o demasiado pequeño
            if (mCurrentViewport.width() * scaleFactor > MAX_WIDTH) {
                scaleFactor = MAX_WIDTH / mCurrentViewport.width()
            } else if (mCurrentViewport.width() * scaleFactor < MIN_WIDTH) {
                scaleFactor = MIN_WIDTH / mCurrentViewport.width()
            }

            // Aplicar el escalamiento al gráfico
            mCurrentViewport.left *= scaleFactor
            mCurrentViewport.right *= scaleFactor
            mCurrentViewport.top *= scaleFactor
            mCurrentViewport.bottom *= scaleFactor

            // Invalidar la vista para que se actualice el gráfico
            graphView.invalidate()

            return true
        }
    }

    companion object {
        private const val MAX_WIDTH = 10f // Definir el máximo ancho del gráfico
        private const val MIN_WIDTH = 0.1f // Definir el mínimo ancho del gráfico
        private val mCurrentViewport = RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX)
        private const val AXIS_X_MIN = 0f // Definir el mínimo del eje X
        private const val AXIS_Y_MIN = 0f // Definir el mínimo del eje Y
        private const val AXIS_X_MAX = 100f // Definir el máximo del eje X
        private const val AXIS_Y_MAX = 100f // Definir el máximo del eje Y
    }
}
