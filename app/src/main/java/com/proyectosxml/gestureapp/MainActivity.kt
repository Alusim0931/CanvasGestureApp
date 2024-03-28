package com.proyectosxml.gestureapp

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

class MainActivity : AppCompatActivity() {
    private var imageAppeared = false
    private var finalImageX = 0f
    private var finalImageY = 0f
    private var savedImageX = 0f
    private var savedImageY = 0f

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var screenBounds: ScreenBounds
    private lateinit var imageScaler: ImageScaler

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvasScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage)

        editableImage.isEnabled = false // Deshabilitar el botón al inicio

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val bottomBar = findViewById<View>(R.id.bottomAppBar)

        imageScaler = ImageScaler(this)
        imageScaler.scaleImage(imageView)

        screenBounds = ScreenBounds(frameLayout, bottomBar)

        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (imageView.scaleType != ImageView.ScaleType.MATRIX) {
                    imageView.scaleType = ImageView.ScaleType.MATRIX
                }

                val scaleFactor = detector.scaleFactor
                imageView.scaleX *= scaleFactor
                imageView.scaleY *= scaleFactor

                // Restablecer el punto de pivote al centro de la imagen
                imageView.pivotX = (imageView.width / 2).toFloat()
                imageView.pivotY = (imageView.height / 2).toFloat()

                return true
            }
        })

        imageView.setOnTouchListener { view, event ->
            mScaleGestureDetector.onTouchEvent(event)
            true
        }

        appearImage.setOnClickListener {
            if (!imageAppeared) {
                mainCanvasScreen.visibility = View.VISIBLE
                imageAppeared = true
                editableImage.isEnabled = true // Habilitar el botón cuando se pulse appearImage
            } else {
                appearImage.isSelected = !appearImage.isSelected
            }
        }

        editableImage.setOnClickListener {
            // Aquí cambias el icono del ImageButton editableImage
            val newIcon = if (editableImage.tag == "normal") {
                R.drawable.baseline_waving_hand_24
            } else {
                R.drawable.baseline_back_hand_24
            }
            editableImage.setImageResource(newIcon)
            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)

            // Cambiar la etiqueta (tag) del ImageButton
            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            // Cambiar la visibilidad del frameLayout e imageView
            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE

            // Si el frameLayout e imageView son visibles, configura el onTouchListener para mover la imagen
            if (editableImage.tag == "pressed") {
                imageView.setOnTouchListener { view, motionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Comprueba si el toque ocurrió dentro de los límites de la imagen
                            if (motionEvent.x < 0 || motionEvent.y < 0 || motionEvent.x > view.width || motionEvent.y > view.height) {
                                return@setOnTouchListener false
                            }
                            // Guarda las coordenadas iniciales del toque
                            savedImageX = motionEvent.rawX - view.x
                            savedImageY = motionEvent.rawY - view.y
                        }

                        MotionEvent.ACTION_MOVE -> {
                            // Verificar que solo hay un dedo en la pantalla
                            if (motionEvent.pointerCount == 1) {
                                // Calcula el desplazamiento desde el toque inicial
                                val dx = motionEvent.rawX - savedImageX
                                val dy = motionEvent.rawY - savedImageY

                                // Verificar que la nueva posición esté dentro de los límites de la pantalla
                                if (screenBounds.isInsideBounds(view, dx, dy)) {
                                    // Mueve la imagen
                                    view.x = dx
                                    view.y = dy
                                }
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            // Guarda las coordenadas finales de la imagen
                            finalImageX = view.x
                            finalImageY = view.y
                        }
                    }
                    true
                }
            }

            // Obtén el tamaño del canvas
            val canvasSize = mainCanvasScreen.getCanvasSize()

            // Escala cada imagen al tamaño del canvas y las añade a la vista
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)
            imageView.setImageBitmap(scaledBitmap)

            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)
        }
    }
}