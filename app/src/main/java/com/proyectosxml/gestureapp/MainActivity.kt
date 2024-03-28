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

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mRotateGestureDetector: RotateGestureDetector

    private var savedImageX = 0f
    private var savedImageY = 0f



    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvaScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)

        val editableImage = findViewById<ImageButton>(R.id.editableImage)
        editableImage.isEnabled = false // Deshabilitar el botón al inicio

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        val imageView = findViewById<ImageView>(R.id.imageView)



        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                imageView.scaleX *= scaleFactor
                imageView.scaleY *= scaleFactor
                return true
            }
        })

        mRotateGestureDetector = RotateGestureDetector(this, object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(angle: Float) {
                imageView.rotation += Math.toDegrees(angle.toDouble()).toFloat()
            }
        })

        appearImage.setOnClickListener {
            if (!imageAppeared) {
                mainCanvaScreen.visibility = View.VISIBLE
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
            mainCanvaScreen.setImageCoordinates(finalImageX, finalImageY)

            // Cambiar la etiqueta (tag) del ImageButton
            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            // Cambiar la visibilidad del frameLayout e imageView
            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = visibility

            // Si el frameLayout e imageView son visibles, configura el onTouchListener para mover la imagen
            imageView.setOnTouchListener { view, motionEvent ->
                mScaleGestureDetector.onTouchEvent(motionEvent)
                mRotateGestureDetector.onTouchEvent(motionEvent)
                if (frameLayout.visibility == View.VISIBLE) {
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
                } else {
                    false // Para que el onTouchListener no se active cuando el frameLayout no sea visible
                }
            }
        }

        // Obtén el tamaño del canvas
        val canvasSize = mainCanvaScreen.getCanvasSize()

        // Escala cada imagen al tamaño del canvas y las añade a la vista
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)
        imageView.setImageBitmap(scaledBitmap)

        mainCanvaScreen.setImageCoordinates(finalImageX, finalImageY)


    }
}