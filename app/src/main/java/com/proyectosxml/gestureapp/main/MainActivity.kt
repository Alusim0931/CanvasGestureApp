package com.proyectosxml.gestureapp.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proyectosxml.gestureapp.gestures.GestureHandler
import com.proyectosxml.gestureapp.R
import com.proyectosxml.gestureapp.dataclass.ImageState

class MainActivity : AppCompatActivity() {

    private lateinit var gestureHandler: GestureHandler
    private lateinit var imageState: ImageState
    private lateinit var imageView: ImageView
    private var secondImageView: ImageView? = null

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvasScreen = findViewById<MainCanvasScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage).apply {
            isEnabled = false
        }

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        imageView = findViewById(R.id.imageView)

        imageState = ImageState()

        gestureHandler = GestureHandler(this, imageView, imageState)

        imageView.setOnTouchListener { view, event ->
            gestureHandler.handleTouchEvent(view, event)
        }

        appearImage.setOnClickListener {
            if (!imageState.imageAppeared) {
                imageState.imageAppeared = true
                editableImage.isEnabled = true
                mainCanvasScreen.visibility =
                    if (mainCanvasScreen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                appearImage.isEnabled = false
            }
        }

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

            imageState.frameImageX = imageState.finalImageX
            imageState.frameImageY = imageState.finalImageY

            imageView.x = imageState.frameImageX
            imageView.y = imageState.frameImageY

            imageView.bringToFront()

            if (editableImage.tag == "pressed") {
                gestureHandler.setupImageTouchListener(imageView)
            }
            mainCanvasScreen.setImageCoordinates(imageView.x, imageView.y)

            val canvasSize = mainCanvasScreen.getCanvasSize()
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap =
                Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

            secondImageView?.let {
                if (it.parent != null) {
                    (it.parent as ViewGroup).removeView(it)
                }
            }

            secondImageView = ImageView(this).apply {
                setImageBitmap(scaledBitmap)

                val layoutParams = FrameLayout.LayoutParams(canvasSize.width, canvasSize.height)
                this.layoutParams = layoutParams
                scaleType = ImageView.ScaleType.CENTER_CROP

                // Guardar el estado de la segunda imagen en el estado de la aplicación
                imageState.secondImageX = layoutParams.leftMargin.toFloat()
                imageState.secondImageY = layoutParams.topMargin.toFloat()
                imageState.secondImageScaleX = scaleX
                imageState.secondImageScaleY = scaleY
                imageState.secondImageRotation = rotation
            }

            frameLayout.addView(secondImageView) // Agregar la segunda imagen al FrameLayout

            if (imageView.parent != null) {
                (imageView.parent as ViewGroup).removeView(imageView)
            }
            frameLayout.addView(imageView) // Agregar la imagen principal al FrameLayout
        }
    }
}
