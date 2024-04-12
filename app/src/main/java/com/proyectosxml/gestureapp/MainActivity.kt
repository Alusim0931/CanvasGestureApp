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
import androidx.appcompat.app.AppCompatActivity
import com.proyectosxml.gestureapp.dataclass.GestureState
import com.proyectosxml.gestureapp.dataclass.ImageState

/**
 * Main activity class that handles image manipulation gestures.
 */
class MainActivity : AppCompatActivity() {
    // Image state
    private var imageState = ImageState()

    // Gesture detectors
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var screenBounds: ScreenBounds
    private lateinit var rotateGestureDetector: RotateGestureDetector

    // Image views
    private lateinit var imageView: ImageView
    private lateinit var staticImageView: ImageView
    private var canvasImage: Bitmap? = null

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the views
        setupViews()
    }

    /**
     * Sets up the views and gesture detectors.
     */
    @SuppressLint("ClickableViewAccessibility", "CutPasteId")
    private fun setupViews() {
        // Buttons and views
        val appearImage = findViewById<ImageButton>(R.id.appearImage)
        val mainCanvasScreen = findViewById<MainCanvaScreen>(R.id.imageScreen)
        val editableImage = findViewById<ImageButton>(R.id.editableImage).apply {
            isEnabled = false
        }

        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout)
        imageView = findViewById(R.id.imageView)
        staticImageView = findViewById(R.id.imageView)
        val bottomBar = findViewById<View>(R.id.bottomAppBar)

        // Gesture detectors
        mScaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor
                    if (scaleFactor > 1 || imageView.scaleX * scaleFactor > 1) {
                        imageView.scaleX *= scaleFactor
                        imageView.scaleY *= scaleFactor

                        // Actualiza el escalado en el canvas
                        mainCanvasScreen.setImageScaleX(imageView.scaleX)
                        mainCanvasScreen.setImageScaleY(imageView.scaleY)
                    }

                    return true
                }
            })

        screenBounds = ScreenBounds(frameLayout, bottomBar)
        rotateGestureDetector = RotateGestureDetector(object : RotateGestureDetector.OnRotateGestureListener {
            override fun onRotate(rotation: Float, imageView: ImageView) {
                if (imageView.x >= 0 && imageView.y >= 0 && imageView.x + imageView.width <= imageView.rootView.width && imageView.y + imageView.height <= imageView.rootView.height) {
                    imageView.rotation += rotation
                    // Aplicar la misma rotación al com.proyectosxml.gestureapp.MainCanvaScreen
                    mainCanvasScreen.setImageRotation(imageView.rotation)
                }
            }
        }, imageView, screenBounds)

        // Touch event handler for the image
        imageView.setOnTouchListener { view, event ->
            handleTouchEvent(view, event)
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
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

            canvasImage = mainCanvasScreen.getBitmap()

            imageView.setImageBitmap(canvasImage)

            imageState.frameImageX = imageState.finalImageX
            imageState.frameImageY = imageState.finalImageY

            imageView.x = imageState.frameImageX
            imageView.y = imageState.frameImageY

            imageView.bringToFront()

            // Set up the touch gesture detector for the image
            if (editableImage.tag == "pressed") {
                setupImageTouchListener(imageView)
            }

            mainCanvasScreen.setImageCoordinates(imageView.x, imageView.y)
        }
    }

    /**
     * Handles touch events.
     * @param view The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about the event.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun handleTouchEvent(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                imageState.currentGesture = GestureState.MOVE
                imageState.savedImageX = event.rawX - view.x
                imageState.savedImageY = event.rawY - view.y
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!imageState.isGestureInProgress) {
                    imageState.isGestureInProgress = true
                    imageState.currentGesture = if (event.pointerCount == 2) GestureState.SCALE_AND_ROTATE else GestureState.NONE
                }
            }

            MotionEvent.ACTION_MOVE -> {
                when (imageState.currentGesture) {
                    GestureState.MOVE -> {
                        val dx = event.rawX - imageState.savedImageX
                        val dy = event.rawY - imageState.savedImageY

                        if (screenBounds.isInsideBounds(view, dx, dy)) {
                            view.x = dx
                            view.y = dy
                            imageState.finalImageX = dx
                            imageState.finalImageY = dy
                        }
                    }
                    GestureState.SCALE_AND_ROTATE -> {
                        mScaleGestureDetector.onTouchEvent(event)
                        rotateGestureDetector.onTouchEvent(event)
                    }
                    else -> {}
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    imageState.currentGesture = GestureState.MOVE
                    imageState.isGestureInProgress = false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                imageState.currentGesture = GestureState.NONE
                imageState.isGestureInProgress = false
            }
        }
        return true
    }

    /**
     * Sets up the touch listener for the image view.
     * @param view The ImageView to set the touch listener on.
     */
    private fun setupImageTouchListener(view: ImageView) {
        // TODO: Implement the functionality of this method
    }
}