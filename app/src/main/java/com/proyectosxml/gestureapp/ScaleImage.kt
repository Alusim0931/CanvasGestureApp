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

    private inner class ScaleListener(private val imageView: ImageView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor

            // Aplicar el escalado
            imageView.scaleX *= scaleFactor
            imageView.scaleY *= scaleFactor

            return true
        }
    }
}