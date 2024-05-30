package com.proyectosxml.gestureapp.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.widget.ImageView
import com.proyectosxml.gestureapp.gestures.RotationGestureDetector

class MoveView : FrameLayout {

    private var screenWidth: Int
    private var screenHeight: Int
    private var gestureScaleDetector: ScaleGestureDetector
    private var gestureMoveDetector: GestureDetector
    private var gestureRotationDetector: RotationGestureDetector
    private var isMoveModeEnabled: Boolean = false
    private var selectImage: ImageView? = null
    private lateinit var viewImageView: ImageView
    private lateinit var imageFrame: FrameLayout

    var moveViewListener: MoveViewListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        gestureScaleDetector = ScaleGestureDetector(context, MyScaleGestureListener())
        gestureMoveDetector = GestureDetector(context, MyGestureListener())
        gestureRotationDetector = RotationGestureDetector(MyRotationGesture())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (isMoveModeEnabled && selectImage == null) {
                val image = findSelectedImage(event.x, event.y)
                image?.let {
                    Log.d("FLOATINGVIEW", "select image")
                    addImage(it)
                }
            }

            gestureScaleDetector.onTouchEvent(event)
            gestureMoveDetector.onTouchEvent(event)
            gestureRotationDetector.onTouchEvent(event)

            if (isMoveModeEnabled) return true
        }

        return false
    }

    private inner class MyScaleGestureListener :
        ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(event: ScaleGestureDetector): Boolean {
            if (selectImage != null) {
                val scaleFactor = event.scaleFactor

                val scaleX = (scaleFactor * viewImageView.scaleX).coerceIn(0.3f, 4f)
                val scaleY = (scaleFactor * viewImageView.scaleY).coerceIn(0.3f, 4f)

                if (isInBounds(
                        viewImageView.x,
                        viewImageView.y,
                        viewImageView.rotation,
                        scaleX,
                        scaleY,
                        imageFrame
                    )
                ) {
                    viewImageView.scaleX = scaleX
                    viewImageView.scaleY = scaleY
                }

                return true
            }

            return false
        }
    }

    private inner class MyRotationGesture : RotationGestureDetector.OnRotationGestureListener {
        override fun onRotation(rotationDetector: RotationGestureDetector?) {
            if (selectImage != null) {
                val rotation = rotationDetector?.getAngle() ?: 0f

                if (isInBounds(
                        viewImageView.x,
                        viewImageView.y,
                        viewImageView.rotation,
                        viewImageView.scaleX,
                        viewImageView.scaleY,
                        imageFrame
                    )
                ) {
                    viewImageView.rotation = rotation
                }
            }
        }
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e1 != null && selectImage != null) {
                val newX = viewImageView.x - distanceX
                val newY = viewImageView.y - distanceY

                if (isInBounds(
                        newX,
                        newY,
                        viewImageView.rotation,
                        viewImageView.scaleX,
                        viewImageView.scaleY,
                        imageFrame
                    )
                ) {
                    viewImageView.x = newX
                    viewImageView.y = newY
                }

                return true
            }

            return false
        }
    }

    private fun findSelectedImage(x: Float, y: Float): ImageView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ImageView) {
                if (x >= child.left && x <= child.right && y >= child.top && y <= child.bottom) {
                    moveViewListener?.onImageSelected(child as ImageView)
                    return child
                }
            }
        }
        return null
    }

    fun addImage(image: ImageView) {
        this.addView(image)
        selectImage = image
    }

    fun isInBounds(
        newX: Float,
        newY: Float,
        rotation: Float,
        scaleX: Float,
        scaleY: Float,
        viewImageView: FrameLayout
    ): Boolean {
        val containerWidth = viewImageView.width
        val containerHeight = viewImageView.height

        val rect = RectF(0f, 0f, viewImageView.width.toFloat(), viewImageView.height.toFloat())
        val matrix = Matrix()
        matrix.postRotate(rotation, rect.centerX(), rect.centerY())
        matrix.postScale(scaleX, scaleY, rect.centerX(), rect.centerY())
        matrix.mapRect(rect)

        val vertices = floatArrayOf(
            rect.left + newX,
            rect.top + newY,
            rect.right + newX,
            rect.top + newY,
            rect.right + newX,
            rect.bottom + newY,
            rect.left + newX,
            rect.bottom + newY
        )

        for (i in vertices.indices step 2) {
            val x = vertices[i]
            val y = vertices[i + 1]
            if (x < 0 || x > containerWidth || y < 0 || y > containerHeight) {
                return false
            }
        }

        return true
    }

    fun setMoveMode(enabled: Boolean) {
        isMoveModeEnabled = enabled
    }

    fun setImageCoordinates(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun getCanvasSize(): Size {
        return Size(width, height)
    }

    fun setBitmap(bitmap: Bitmap) {
        if (this.childCount > 0) {
            val child = this.getChildAt(0)
            if (child is ImageView) {
                child.setImageBitmap(bitmap)
            }
        }
    }

    data class Size(val width: Int, val height: Int)

    interface MoveViewListener {
        fun onImageSelected(image: ImageView)
    }
}
