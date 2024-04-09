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

// Clase principal de la actividad
class MainActivity : AppCompatActivity() {
    private var imageAppeared = false
    private var finalImageX = 0f
    private var finalImageY = 0f
    private var savedImageX = 0f
    private var savedImageY = 0f
    private var isGestureInProgress = false
    private var currentGesture = GestureState.NONE

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var screenBounds: ScreenBounds
    private lateinit var rotateGestureDetector: RotateGestureDetector
    private lateinit var graphScaler: GraphScaler

    private lateinit var imageView: ImageView
    private var canvasImage: Bitmap? = null

    private var frameImageX = 0f
    private var frameImageY = 0f

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val bottomBar = findViewById<View>(R.id.bottomAppBar)

        mScaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    if (imageView.scaleType != ImageView.ScaleType.MATRIX) {
                        imageView.scaleType = ImageView.ScaleType.MATRIX
                    }

                    val scaleFactor = detector.scaleFactor
                    imageView.scaleX *= scaleFactor
                    imageView.scaleY *= scaleFactor

                    imageView.pivotX = detector.focusX
                    imageView.pivotY = detector.focusY

                    return true
                }
            })

        screenBounds = ScreenBounds(frameLayout, bottomBar)
        rotateGestureDetector =
            RotateGestureDetector(object : RotateGestureDetector.OnRotateGestureListener {
                override fun onRotate(rotation: Float, imageView: ImageView) {
                    if (imageView.x >= 0 && imageView.y >= 0 && imageView.x + imageView.width <= imageView.rootView.width && imageView.y + imageView.height <= imageView.rootView.height) {
                        imageView.rotation += rotation
                    }
                }
            }, imageView, screenBounds)

        graphScaler = GraphScaler(this)
        graphScaler.scaleImage(imageView)

        imageView.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    currentGesture = GestureState.MOVE
                    savedImageX = event.rawX - view.x
                    savedImageY = event.rawY - view.y
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    currentGesture = if (event.pointerCount == 2) GestureState.ROTATE else GestureState.SCALE
                }

                MotionEvent.ACTION_MOVE -> {
                    when (currentGesture) {
                        GestureState.MOVE -> {
                            val dx = event.rawX - savedImageX
                            val dy = event.rawY - savedImageY

                            if (screenBounds.isInsideBounds(view, dx, dy)) {
                                view.x = dx
                                view.y = dy
                                finalImageX = dx
                                finalImageY = dy
                            }
                        }

                        GestureState.SCALE -> {
                            mScaleGestureDetector.onTouchEvent(event)
                        }

                        GestureState.ROTATE -> {
                            rotateGestureDetector.onTouchEvent(event)
                        }

                        else -> {}
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    if (event.pointerCount <= 1) {
                        currentGesture = GestureState.MOVE
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    currentGesture = GestureState.NONE
                }
            }
            true
        }

        appearImage.setOnClickListener {
            if (!imageAppeared) {
                imageAppeared = true
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
            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)

            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE

            val canvasSize = mainCanvasScreen.getCanvasSize()
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

            // Guarda la imagen del canvas en la variable
            canvasImage = mainCanvasScreen.getBitmap()

            // Muestra la imagen en el FrameLayout
            imageView.setImageBitmap(canvasImage)

            // Guardar las coordenadas de la imagen en el FrameLayout
            frameImageX = finalImageX
            frameImageY = finalImageY

            // Restaurar las coordenadas de la imagen en el FrameLayout
            imageView.x = frameImageX
            imageView.y = frameImageY

            // Asegúrate de que la ImageView no esté en la parte superior de otros elementos interactivos
            imageView.bringToFront()

            if (editableImage.tag == "pressed") {
                setupImageTouchListener(imageView)
            }

            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupImageTouchListener(imageView: ImageView) {
        imageView.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    currentGesture = GestureState.MOVE
                    savedImageX = event.rawX - view.x
                    savedImageY = event.rawY - view.y
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (!isGestureInProgress) {
                        currentGesture = if (event.pointerCount == 2) GestureState.ROTATE else GestureState.SCALE
                        isGestureInProgress = true
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    when (currentGesture) {
                        GestureState.MOVE -> {
                            val dx = event.rawX - savedImageX
                            val dy = event.rawY - savedImageY

                            if (screenBounds.isInsideBounds(view, dx, dy)) {
                                view.x = dx
                                view.y = dy
                                finalImageX = dx
                                finalImageY = dy
                            }
                        }
                        GestureState.SCALE -> {
                            if (isGestureInProgress) {
                                mScaleGestureDetector.onTouchEvent(event)
                            }
                        }

                        GestureState.ROTATE -> {
                            if (isGestureInProgress) {
                                rotateGestureDetector.onTouchEvent(event)
                            }
                        }

                        else -> {}
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    if (event.pointerCount <= 1) {
                        currentGesture = GestureState.MOVE
                        isGestureInProgress = false
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    currentGesture = GestureState.NONE
                    isGestureInProgress = false
                }
            }
            true
        }
    }
}