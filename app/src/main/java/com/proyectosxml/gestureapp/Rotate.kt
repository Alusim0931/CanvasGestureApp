package com.proyectosxml.gestureapp

import android.view.MotionEvent
import android.widget.ImageView
import java.lang.StrictMath.sqrt
import kotlin.math.abs
import kotlin.math.atan2

class RotateGestureDetector(
    private val listener: OnRotateGestureListener,
    private val imageView: ImageView,
    private val screenBounds: ScreenBounds
) {
    private var prevX1 = 0f
    private var prevY1 = 0f
    private var prevX2 = 0f
    private var prevY2 = 0f
    private var initialAngle = 0.0

    // Umbral mínimo para la distancia de desplazamiento que debe alcanzar el gesto para considerarse una rotación
    private val rotationThreshold = 10f

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    val x1 = event.getX(0)
                    val y1 = event.getY(0)
                    val x2 = event.getX(1)
                    val y2 = event.getY(1)

                    // Verifica si ambos dedos están dentro del rango de la imagen
                    if (isWithinBounds(x1, y1) && isWithinBounds(x2, y2)) {
                        prevX1 = x1
                        prevY1 = y1
                        prevX2 = x2
                        prevY2 = y2
                        initialAngle = Math.toDegrees(atan2(y2 - y1, x2 - x1).toDouble())
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val x1 = event.getX(0)
                    val y1 = event.getY(0)
                    val x2 = event.getX(1)
                    val y2 = event.getY(1)

                    // Verifica si ambos dedos están dentro del rango de la imagen
                    if (isWithinBounds(x1, y1) && isWithinBounds(x2, y2)) {
                        val deltaX1 = x1 - prevX1
                        val deltaY1 = y1 - prevY1
                        val deltaX2 = x2 - prevX2
                        val deltaY2 = y2 - prevY2

                        // Calcula la distancia total de desplazamiento
                        val distanceMoved = Math.sqrt(
                            (deltaX1 * deltaX1 + deltaY1 * deltaY1 + deltaX2 * deltaX2 + deltaY2 * deltaY2).toDouble()
                        )

                        // Calcula el ángulo actual
                        val angle = Math.toDegrees(atan2(y2 - y1, x2 - x1).toDouble())

                        // Calcula la diferencia de ángulos entre el ángulo actual y el ángulo inicial
                        val angleDifference = angle - initialAngle

                        // Si la distancia de desplazamiento supera el umbral y la dirección es significativa,
                        // consideramos que se trata de un gesto de rotación
                        if (distanceMoved > rotationThreshold && abs(angleDifference) > rotationThreshold) {
                            listener.onRotate(angleDifference.toFloat(), imageView)
                        }

                        prevX1 = x1
                        prevY1 = y1
                        prevX2 = x2
                        prevY2 = y2
                    }
                }
            }
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                prevX1 = 0f
                prevY1 = 0f
                prevX2 = 0f
                prevY2 = 0f
                initialAngle = 0.0
            }
        }
        return true
    }

    // Verifica si las coordenadas dadas están dentro del rango de la imagen
    private fun isWithinBounds(x: Float, y: Float): Boolean {
        return x >= 0 && x <= imageView.width && y >= 0 && y <= imageView.height
    }

    interface OnRotateGestureListener {
        fun onRotate(rotation: Float, imageView: ImageView)
    }
}