package com.proyectosxml.gestureapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var imageAppeared = false
    private var finalImageX = 0f
    private var finalImageY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvaScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage)
        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val imageView = findViewById<ImageView>(R.id.imageView)

        appearImage.setOnClickListener {
            if (!imageAppeared) {
                mainCanvaScreen.visibility = View.VISIBLE
                imageAppeared = true
            } else {
                // Cambia el estado del botón cuando interactúas con él
                appearImage.isSelected = !appearImage.isSelected

            }
        }

        editableImage.setOnClickListener {
            // Aquí cambias el icono del ImageButton editableImage
            val newIcon = if (editableImage.tag == "normal") {
                R.drawable.baseline_back_hand_24
            } else {
                R.drawable.baseline_waving_hand_24
            }
            editableImage.setImageResource(newIcon)
            mainCanvaScreen.setImageCoordinates(finalImageX, finalImageY)

            // Cambiar la etiqueta (tag) del ImageButton
            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            // Cambiar la visibilidad del frameLayout
            frameLayout.visibility = if (editableImage.tag == "normal") View.VISIBLE else View.GONE
        }

        // Obtén el tamaño del canvas
        val canvasSize = mainCanvaScreen.getCanvasSize()

        // Escala cada imagen al tamaño del canvas y las añade a la vista
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)
        imageView.setImageBitmap(scaledBitmap)

        // Configura el listener de toque para la imagen
        imageView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Guarda las coordenadas iniciales del toque
                    view.tag = floatArrayOf(motionEvent.x, motionEvent.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    // Calcula el desplazamiento desde el toque inicial
                    val initialTouch = view.tag as FloatArray
                    val dx = motionEvent.x - initialTouch[0]
                    val dy = motionEvent.y - initialTouch[1]

                    // Mueve la imagen
                    view.x += dx
                    view.y += dy
                }
                MotionEvent.ACTION_UP -> {
                    // Guarda las coordenadas finales de la imagen
                    finalImageX = view.x
                    finalImageY = view.y
                }
            }
            true
        }

        mainCanvaScreen.setImageCoordinates(finalImageX, finalImageY)
    }
}
