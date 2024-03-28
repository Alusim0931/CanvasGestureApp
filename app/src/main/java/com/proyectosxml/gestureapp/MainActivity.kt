package com.proyectosxml.gestureapp

import RotateGestureDetector
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Clase principal de la actividad
class MainActivity : AppCompatActivity() {
    // Variables para controlar el estado de la imagen
    private var imageAppeared = false
    private var finalImageX = 0f
    private var finalImageY = 0f
    private var savedImageX = 0f
    private var savedImageY = 0f

    // Detectores de gestos para escalar y rotar la imagen
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var screenBounds: ScreenBounds
    private lateinit var rotateGestureDetector: RotateGestureDetector

    // Método que se llama cuando se crea la actividad
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configura las vistas y los detectores de gestos
        setupViews()
    }

    // Método para configurar las vistas y los detectores de gestos
    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        // Encuentra las vistas en el layout
        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvasScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage).apply {
            isEnabled = false
        }

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val bottomBar = findViewById<View>(R.id.bottomAppBar)

        // Configura el detector de gestos de escala
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (imageView.scaleType != ImageView.ScaleType.MATRIX) {
                    imageView.scaleType = ImageView.ScaleType.MATRIX
                }

                val scaleFactor = detector.scaleFactor
                imageView.scaleX *= scaleFactor
                imageView.scaleY *= scaleFactor

                // Calcula el pivote de escala como el punto focal del gesto
                imageView.pivotX = detector.focusX
                imageView.pivotY = detector.focusY

                return true
            }
        })

        // Configura el detector de gestos de rotación
        rotateGestureDetector = RotateGestureDetector(object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(rotation: Float) {
                imageView.rotation += rotation
            }
        })

        // Configura el onTouchListener para la vista de la imagen
        imageView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            rotateGestureDetector.onTouchEvent(event)
            true
        }
        // En el método onScale, calcula el pivote de escala correctamente y aplica la escala
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (imageView.scaleType != ImageView.ScaleType.MATRIX) {
                    imageView.scaleType = ImageView.ScaleType.MATRIX
                }

                val scaleFactor = detector.scaleFactor
                imageView.scaleX *= scaleFactor
                imageView.scaleY *= scaleFactor

                // Calcula el pivote de escala como el punto focal del gesto
                imageView.pivotX = detector.focusX
                imageView.pivotY = detector.focusY

                return true
            }
        })


        // Configura el detector de gestos de rotación
        rotateGestureDetector = RotateGestureDetector(object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(rotation: Float) {
                imageView.rotation += rotation
            }
        })

        // Configura el onTouchListener para la vista de la imagen
        imageView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            rotateGestureDetector.onTouchEvent(event)
            true
        }

        screenBounds = ScreenBounds(frameLayout, bottomBar)

        // Configura el onClickListener para el botón de aparición de la imagen
        appearImage.setOnClickListener {
            if (!imageAppeared) {
                imageAppeared = true
                editableImage.isEnabled = true
                mainCanvasScreen.visibility = if (mainCanvasScreen.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                appearImage.isEnabled = false // Deshabilita el botón después de hacer clic en él
            }
        }

        // Configura el onClickListener para el botón de edición de la imagen
        editableImage.setOnClickListener {
            val newIcon = if (editableImage.tag == "normal") {
                R.drawable.baseline_waving_hand_24
            } else {
                R.drawable.baseline_back_hand_24
            }
            editableImage.setImageResource(newIcon)
            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)

            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE

            if (editableImage.tag == "pressed") {
                setupImageTouchListener(imageView)
            }

            val canvasSize = mainCanvasScreen.getCanvasSize()

            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)
            imageView.setImageBitmap(scaledBitmap)

            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)
        }
    }

    // Método para configurar el onTouchListener para la vista de la imagen
    @SuppressLint("ClickableViewAccessibility")
    private fun setupImageTouchListener(imageView: ImageView) {
        imageView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (motionEvent.x < 0 || motionEvent.y < 0 || motionEvent.x > view.width || motionEvent.y > view.height) {
                        return@setOnTouchListener false
                    }
                    savedImageX = motionEvent.rawX - view.x
                    savedImageY = motionEvent.rawY - view.y
                }

                MotionEvent.ACTION_MOVE -> {
                    if (motionEvent.pointerCount == 1) {
                        val dx = motionEvent.rawX - savedImageX
                        val dy = motionEvent.rawY - savedImageY

                        if (screenBounds.isInsideBounds(view, dx, dy)) {
                            view.x = dx
                            view.y = dy
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    finalImageX = view.x
                    finalImageY = view.y
                }
            }
            true
        }
    }
}