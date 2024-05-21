package com.proyectosxml.gestureapp.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proyectosxml.gestureapp.gestures.GestureHandler
import com.proyectosxml.gestureapp.R
import com.proyectosxml.gestureapp.dataclass.ImageState
import com.proyectosxml.gestureapp.extras.ImagePositionManager

class MainActivity : AppCompatActivity() {

    private lateinit var gestureHandler: GestureHandler
    private lateinit var imageState: ImageState
    private lateinit var imageView: ImageView
    private var secondImageView: ImageView? = null
    private lateinit var imagePositionManager: ImagePositionManager

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imagePositionManager = ImagePositionManager(this)
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

        gestureHandler = GestureHandler(this, imageView, imageState, secondImageView)

        imageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE) {
                imageState.lastImageX = view.x
                imageState.lastImageY = view.y
                secondImageView?.let {
                    imageState.secondImageX = it.x
                    imageState.secondImageY = it.y
                }
                // Guardar la posición actual de las imágenes
                imagePositionManager.saveImagePosition(imageView, ImagePositionManager.KEY_IMAGE1_X, ImagePositionManager.KEY_IMAGE1_Y)
                secondImageView?.let {
                    imagePositionManager.saveImagePosition(it, ImagePositionManager.KEY_IMAGE2_X, ImagePositionManager.KEY_IMAGE2_Y)
                }
            }
            gestureHandler.handleTouchEvent(view, event)
            true
        }

        appearImage.setOnClickListener {
            if (!imageState.imageAppeared) {
                imageState.imageAppeared = true
                editableImage.isEnabled = true
                mainCanvasScreen.visibility = if (mainCanvasScreen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                appearImage.isEnabled = false

                // Restaurar las coordenadas guardadas de las imágenes
                restoreImagePositions()
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

            if (editableImage.tag == "pressed") {
                gestureHandler.setupImageTouchListener(imageView)
            }
            mainCanvasScreen.setImageCoordinates(imageView.x, imageView.y)

            val canvasSize = mainCanvasScreen.getCanvasSize()
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

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

                // Restaurar el estado de la segunda imagen
                this.x = imageState.secondImageX
                this.y = imageState.secondImageY
                this.scaleX = imageState.secondImageScaleX
                this.scaleY = imageState.secondImageScaleY
                this.rotation = imageState.secondImageRotation
            }

            frameLayout.addView(secondImageView)

            if (imageView.parent != null) {
                (imageView.parent as ViewGroup).removeView(imageView)
            }

            imageView.apply {
                val layoutParams = FrameLayout.LayoutParams(canvasSize.width, canvasSize.height)
                this.layoutParams = layoutParams
                // Restaurar el estado de la imagen principal
                this.x = imageState.lastImageX
                this.y = imageState.lastImageY
            }

            frameLayout.addView(imageView)
            secondImageView?.let {
                val bitmap = (it.drawable as BitmapDrawable).bitmap
                mainCanvasScreen.setBitmap(bitmap)
            }

            // Guardar la última posición de la imagen en el FrameLayout
            imageState.lastImageX = imageView.x
            imageState.lastImageY = imageView.y

            // Guardar la última posición de la segunda imagen en el FrameLayout
            imageState.secondImageX = secondImageView?.x ?: 0f
            imageState.secondImageY = secondImageView?.y ?: 0f

            // Guardar la posición actual de las imágenes
            imagePositionManager.saveImagePosition(imageView, ImagePositionManager.KEY_IMAGE1_X, ImagePositionManager.KEY_IMAGE1_Y)
            secondImageView?.let {
                imagePositionManager.saveImagePosition(it, ImagePositionManager.KEY_IMAGE2_X, ImagePositionManager.KEY_IMAGE2_Y)
            }
        }
    }

    private fun restoreImagePositions() {
        val positionManager = ImagePositionManager(this)

        val image1Position = positionManager.getImagePosition(ImagePositionManager.KEY_IMAGE1_X, ImagePositionManager.KEY_IMAGE1_Y)
        val image2Position = positionManager.getImagePosition(ImagePositionManager.KEY_IMAGE2_X, ImagePositionManager.KEY_IMAGE2_Y)

        val params1 = imageView.layoutParams as FrameLayout.LayoutParams
        params1.leftMargin = image1Position.x
        params1.topMargin = image1Position.y
        imageView.layoutParams = params1

        secondImageView?.let {
            val params2 = it.layoutParams as FrameLayout.LayoutParams
            params2.leftMargin = image2Position.x
            params2.topMargin = image2Position.y
            it.layoutParams = params2
        }
    }
}
