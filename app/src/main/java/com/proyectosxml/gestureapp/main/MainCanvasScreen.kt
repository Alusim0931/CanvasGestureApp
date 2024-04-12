package com.proyectosxml.gestureapp.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Size
import android.view.View
import com.proyectosxml.gestureapp.R

class MainCanvasScreen(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    // Image position variables
    private var imageX = 10f
    private var imageY = 10f
    private var imageRotation = 0f
    private var imageScaleX = 1f
    private var imageScaleY = 1f

    // Paint object for drawing
    private val paint: Paint = Paint()

    // Bitmap object for the image
    private var mushroom: Bitmap

    // Original bitmap resource
    private val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.rectangulo)

    // Canvas dimensions
    private val canvasHeight = 300
    private val canvasWidth = 300

    // Initialize the view
    init {
        // Set paint color
        paint.color = Color.WHITE
        // Scale the bitmap to fit the canvas
        mushroom = Bitmap.createScaledBitmap(originalBitmap, canvasHeight, canvasWidth, true)
    }

    // Draw the canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Save the current canvas state
        canvas.save()
        // Apply scaling and rotation transformations
        canvas.scale(imageScaleX, imageScaleY, imageX + (mushroom.width / 2), imageY + (mushroom.height / 2))
        canvas.rotate(imageRotation, imageX + (mushroom.width / 2), imageY + (mushroom.height / 2))
        // Draw the bitmap on the canvas
        canvas.drawBitmap(mushroom, imageX, imageY, paint)
        // Restore the canvas to its previous state
        canvas.restore()
    }

    // Get the size of the canvas
    fun getCanvasSize(): Size {
        return Size(canvasWidth, canvasHeight)
    }

    // Set the coordinates of the image
    fun setImageCoordinates(x: Float, y: Float) {
        imageX = x
        imageY = y
        invalidate()
    }

    // Set the rotation angle of the image
    fun setImageRotation(rotation: Float) {
        imageRotation = rotation
        invalidate()
    }

    // Set the size of the image
    fun setImageSize(width: Int, height: Int) {
        // Resize the bitmap
        mushroom = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        invalidate()
    }

    // Get the bitmap of the image
    fun getBitmap(): Bitmap {
        return mushroom
    }

    // Set the bitmap of the image
    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            mushroom = bitmap
            invalidate()
        }
    }

    // Set the horizontal scaling factor of the image
    fun setImageScaleX(scaleX: Float) {
        imageScaleX = scaleX
        invalidate()
    }

    // Set the vertical scaling factor of the image
    fun setImageScaleY(scaleY: Float) {
        imageScaleY = scaleY
        invalidate()
    }
}
