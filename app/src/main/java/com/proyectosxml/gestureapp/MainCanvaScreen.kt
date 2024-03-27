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

    //Variables para pintar el canvas y la imagen
    private val paint: Paint = Paint()
    private var mushroom: Bitmap

    //Variable para mantener la imagen original
    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.campi)

    private val height = 100
    private val width = 100

    // Lista para almacenar las imágenes
    private val imageList = mutableListOf<Int>()

    //Se utiliza el init para inicializar las variables
    init {
        paint.color = Color.WHITE
        mushroom = Bitmap.createScaledBitmap(originalBitmap, height, width, true)
    }

    //Función para dibujar la imagen en el canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mushroom, 10f, 10f, paint)
    }

    //Función para obtener el tamaño del canvas
    fun getCanvasSize(): Size {
        return Size(width, height)
    }

    //Función para añadir una imagen a la lista
    fun addImage(imageResId: Int) {
        imageList.add(imageResId)
    }
}