package com.proyectosxml.gestureapp

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

class MainCanvaScreen(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var imageX = 10f
    private var imageY = 10f
    private var imageRotation = 0f

    private var imageScaleX = 1f
    private var imageScaleY = 1f

    //Variables for paint the canvas and the image
    private val paint: Paint = Paint()
    private var mushroom: Bitmap

    //Variable for hold the original image
    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)

    private val height = 300
    private val width = 300

    //Init for initialize the variables
    init {
        paint.color = Color.WHITE
        mushroom = Bitmap.createScaledBitmap(originalBitmap, height, width, true)
    }

    //Function for draw the image in the canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.scale(imageScaleX, imageScaleY, imageX + (mushroom.width / 2), imageY + (mushroom.height / 2))
        canvas.rotate(imageRotation, imageX + (mushroom.width / 2), imageY + (mushroom.height / 2))
        canvas.drawBitmap(mushroom, imageX, imageY, paint) // Dibuja la imagen en las coordenadas actuales
        canvas.restore()
    }

    //Function for obtain the height of canvas
    fun getCanvasSize(): Size {
        return Size(width, height)
    }

    fun setImageCoordinates(x: Float, y: Float) {
        imageX = x
        imageY = y
        invalidate() // Draw the canvas with the new coordinates
    }

    fun setImageRotation(rotation: Float) {
        imageRotation = rotation
        invalidate() // Draw the canvas with the new rotation
    }

    fun setImageSize(width: Int, height: Int) {
        mushroom = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        invalidate() // Vuelve a dibujar el canvas con el nuevo tamaño de imagen
    }

    fun getBitmap(): Bitmap {
        return mushroom
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            mushroom = bitmap
            invalidate() // Draw the canvas with the new image
        }
    }

    fun setImageScaleX(scaleX: Float) {
        imageScaleX = scaleX
        invalidate() // Redibuja el canvas con el nuevo escalado
    }

    fun setImageScaleY(scaleY: Float) {
        imageScaleY = scaleY
        invalidate() // Redibuja el canvas con el nuevo escalado
    }

}
