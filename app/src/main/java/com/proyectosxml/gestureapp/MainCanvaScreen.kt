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

class MainCanvaScreen(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var imageX = 10f
    private var imageY = 10f

    //Variables para pintar el canvas y la imagen
    private val paint: Paint = Paint()
    private var mushroom: Bitmap

    //Variable para mantener la imagen original
    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)

    private val height = 100
    private val width = 100

    //Se utiliza el init para inicializar las variables
    init {
        paint.color = Color.WHITE
        mushroom = Bitmap.createScaledBitmap(originalBitmap, height, width, true)
    }

    //Función para dibujar la imagen en el canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mushroom, imageX, imageY, paint) // Dibuja la imagen en las coordenadas actuales
    }

    //Función para obtener el tamaño del canvas
    fun getCanvasSize(): Size {
        return Size(width, height)
    }

    fun setImageCoordinates(x: Float, y: Float) {
        imageX = x
        imageY = y
        invalidate() // Redibuja el canvas con las nuevas coordenadas
    }

    fun getBitmap(): Bitmap {
        return mushroom
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            mushroom = bitmap
            invalidate() // Redibuja el canvas con la nueva imagen
        }
    }
}