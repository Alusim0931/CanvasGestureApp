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

/**
 * Main activity class that handles image manipulation gestures.
 */
class MainActivity : AppCompatActivity() {
    // Variables to control the state of the image
    private var imageAppeared = false
    private var finalImageX = 0f
    private var finalImageY = 0f
    private var savedImageX = 0f
    private var savedImageY = 0f
    private var isGestureInProgress = false
    private var currentGesture = GestureState.NONE

    // Gesture detectors
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var screenBounds: ScreenBounds
    private lateinit var rotateGestureDetector: RotateGestureDetector

    // Image views
    private lateinit var imageView: ImageView
    private lateinit var staticImageView: ImageView
    private var canvasImage: Bitmap? = null

    // Position of the image in the frame
    private var frameImageX = 0f
    private var frameImageY = 0f

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
                    // Get the coordinates of the two fingers
                    val x1 = detector.getFocusX()
                    val y1 = detector.getFocusY()
                    val x2 = detector.getFocusX()
                    val y2 = detector.getFocusY()

                    // Check if both fingers are within the image bounds
                    val isInImageBounds = x1 in 0f..imageView.width.toFloat() && y1 in 0f..imageView.height.toFloat() &&
                            x2 in 0f..imageView.width.toFloat() && y2 in 0f..imageView.height.toFloat()

                    if (isInImageBounds) {
                        val scaleFactor = detector.scaleFactor
                        if (scaleFactor > 1 || imageView.scaleX * scaleFactor > 1) {
                            imageView.scaleX *= scaleFactor
                            imageView.scaleY *= scaleFactor
                        }
                    }

                    return true
                }
            })

        screenBounds = ScreenBounds(frameLayout, bottomBar)
        rotateGestureDetector =
            RotateGestureDetector(object : RotateGestureDetector.OnRotateGestureListener {
                override fun onRotateEnd() {
                    TODO("Not yet implemented")
                }

                override fun onRotate(rotation: Float, imageView: ImageView) {
                    if (imageView.x >= 0 && imageView.y >= 0 && imageView.x + imageView.width <= imageView.rootView.width && imageView.y + imageView.height <= imageView.rootView.height) {
                        imageView.rotation += rotation
                    }
                }
            }, imageView, screenBounds)

        // Touch event handler for the image
        imageView.setOnTouchListener { view, event ->
            handleTouchEvent(view, event)
        }

        // Listener for the image appearance button
        appearImage.setOnClickListener {
            if (!imageAppeared) {
                imageAppeared = true
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
            mainCanvasScreen.setImageCoordinates(finalImageX, finalImageY)

            editableImage.tag = if (editableImage.tag == "normal") "pressed" else "normal"

            val visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE
            frameLayout.visibility = visibility
            imageView.visibility = if (editableImage.tag == "pressed") View.VISIBLE else View.GONE

            val canvasSize = mainCanvasScreen.getCanvasSize()
            val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, canvasSize.width, canvasSize.height, true)

            canvasImage = mainCanvasScreen.getBitmap()

            imageView.setImageBitmap(canvasImage)

            frameImageX = finalImageX
            frameImageY = finalImageY

            imageView.x = frameImageX
            imageView.y = frameImageY

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
                currentGesture = GestureState.MOVE
                savedImageX = event.rawX - view.x
                savedImageY = event.rawY - view.y
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!isGestureInProgress) {
                    isGestureInProgress = true
                    currentGesture = if (event.pointerCount == 2) GestureState.SCALE_AND_ROTATE else GestureState.NONE
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
                    GestureState.SCALE_AND_ROTATE -> {
                        mScaleGestureDetector.onTouchEvent(event)
                        rotateGestureDetector.onTouchEvent(event)
                    }
                    else -> {}
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 1) {
                    currentGesture = GestureState.MOVE
                    isGestureInProgress = false
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentGesture = GestureState.NONE
                isGestureInProgress = false
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