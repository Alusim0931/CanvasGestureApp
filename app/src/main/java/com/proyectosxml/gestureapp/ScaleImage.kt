package com.proyectosxml.gestureapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView

class ImageScaler(private val context: Context) {
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    @SuppressLint("ClickableViewAccessibility")
    fun scaleImage(imageView: ImageView) {
        // Crear un ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener(imageView))

        // Agregar un OnTouchListener a la vista de la imagen
        imageView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            true // Indicar que se ha consumido el evento de toque
        }
    }

    private inner class ScaleListener(private val imageView: ImageView) :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = detector.scaleFactor

            // Limitar el factor de escala para evitar que la imagen se vuelva demasiado grande o demasiado pequeña
            if (imageView.scaleX * scaleFactor > 2.0f) {
                scaleFactor = 2.0f / imageView.scaleX
            } else if (imageView.scaleX * scaleFactor < 0.5f) {
                scaleFactor = 0.5f / imageView.scaleX
            }

            imageView.scaleX *= scaleFactor
            imageView.scaleY *= scaleFactor

            return true
        }
    }
}