package com.proyectosxml.gestureapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var imageAppeared = false

    // Lista para almacenar las imágenes
    private val imageList = listOf(
        R.drawable.campi
    )

    @SuppressLint("WrongViewCast")
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
            }
        }

        // Cambiar el icono del ImageButton editableImage
        editableImage.setOnClickListener {
            // Aquí cambias el icono del ImageButton editableImage
            val newIcon = if (editableImage.tag == "normal") {
                R.drawable.baseline_back_hand_24
            } else {
                R.drawable.baseline_waving_hand_24
            }
            editableImage.setImageResource(newIcon)

            // Cambiar la etiqueta (tag) del ImageButton
            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            // Cambiar la visibilidad del frameLayout
            frameLayout.visibility = if (editableImage.tag == "normal") View.VISIBLE else View.GONE
        }

        // Obtén el tamaño del canvas
        val canvasSize = mainCanvaScreen.getCanvasSize()

        // Escala cada imagen al tamaño del canvas y las añade a la vista
        imageList.forEach { imageResId ->
            val originalBitmap = BitmapFactory.decodeResource(resources, imageResId)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)
            imageView.setImageBitmap(scaledBitmap)
        }
    }
}