package com.proyectosxml.gestureapp.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proyectosxml.gestureapp.gestures.GestureHandler
import com.proyectosxml.gestureapp.R
import com.proyectosxml.gestureapp.dataclass.ImageState

/**
 * Main activity class that handles image manipulation gestures.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var gestureHandler: GestureHandler
    private lateinit var imageState: ImageState
    private lateinit var imageView: ImageView
    private lateinit var canvasImage: Bitmap

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the views
        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvasScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage).apply {
            isEnabled = false
        }

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        imageView = findViewById(R.id.imageView)

        // Initialize image state
        imageState = ImageState()

        // Initialize gesture handler
        gestureHandler = GestureHandler(this, imageView, mainCanvasScreen, imageState)

        // Touch event handler for the image
        imageView.setOnTouchListener { view, event ->
            gestureHandler.handleTouchEvent(view, event)
        }

        // Listener for the image appearance button
        appearImage.setOnClickListener {
            if (!imageState.imageAppeared) {
                imageState.imageAppeared = true
                editableImage.isEnabled = true
                mainCanvasScreen.visibility =
                    if (mainCanvasScreen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                appearImage.isEnabled = false
            }
        }

        // Listener for the image editing button
        editableImage.setOnClickListener {
            val newIcon = if (editableImage.tag == "normal") {
                R.drawable.baseline_waving_hand_24
            } else {
                R.drawable.baseline_back_hand_24
            }
            editableImage.setImageResource(newIcon)
            mainCanvasScreen.setImageCoordinates(imageState.finalImageX, imageState.finalImageY)

            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE

            val canvasSize = mainCanvasScreen.getCanvasSize()
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            canvasImage = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

            imageView.setImageBitmap(canvasImage)

            imageState.frameImageX = imageState.finalImageX
            imageState.frameImageY = imageState.finalImageY

            imageView.x = imageState.frameImageX
            imageView.y = imageState.frameImageY

            imageView.bringToFront()

            // Set up the touch gesture detector for the image
            if (editableImage.tag == "pressed") {
                gestureHandler.setupImageTouchListener(imageView)
            }
            mainCanvasScreen.setImageCoordinates(imageView.x, imageView.y)
        }
    }
}