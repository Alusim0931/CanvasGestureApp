package com.proyectosxml.gestureapp.main

import android.content.Context
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
        //Get tamaño pantalla
        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        //Iniciar Gestures
        gestureScaleDetector = ScaleGestureDetector(context, MyScaleGestureListener())
        gestureMoveDetector = GestureDetector(context, MyGestureListener())
        gestureRotationDetector = RotationGestureDetector(MyRotationGesture())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (isMoveModeEnabled && selectImage == null) {
                //Funcion para saber si has pulsado sobre una imagen
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

                // Aplicar el factor de escala a la imagen
                val scaleX = (scaleFactor * viewImageView.scaleX).coerceIn(0.3f, 4f)
                val scaleY = (scaleFactor * viewImageView.scaleY).coerceIn(0.3f, 4f)

                //Funcion para controlar que no se salga de los bordes de la pantalla
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

                return true // Indica que has manejado completamente el evento de escala
            }

            return false // Indica que no has manejado el evento de escala
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
                // Calcular la nueva posición
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
                    // Actualizar la posición de la imagen
                    viewImageView.x = newX//newPosX
                    viewImageView.y = newY//newPosY
                }

                return true
            }

            return false
        }
    }

    private fun findSelectedImage(x: Float, y: Float): ImageView? {
        // Iterate over all child views (assuming they are ImageViews)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ImageView) {
                // Check if the coordinates are within the child's bounds
                if (x >= child.left && x <= child.right && y >= child.top && y <= child.bottom) {
                    moveViewListener?.onImageSelected(child as ImageView)
                    return child
                }
            }
        }
        // No ImageView was found under the given coordinates
        return null
    }

    fun addImage(image: ImageView) {
        // Add the image to the MoveView
        this.addView(image)
        // Set the selected image
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
        // Obtener las dimensiones del contenedor
        val containerWidth = viewImageView.width
        val containerHeight = viewImageView.height

        // Calcular las coordenadas de los cuatro vértices de la imagen después de la rotación
        val rect = RectF(0f, 0f, viewImageView.width.toFloat(), viewImageView.height.toFloat())
        val matrix = Matrix()
        matrix.postRotate(rotation, rect.centerX(), rect.centerY())
        matrix.postScale(scaleX, scaleY, rect.centerX(), rect.centerY())
        matrix.mapRect(rect)

        // Calcular las coordenadas de los cuatro vértices de la imagen después de la translación
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

        // Verificar si alguno de los vértices está fuera de los límites del contenedor
        for (i in vertices.indices step 2) {
            val x = vertices[i]
            val y = vertices[i + 1]
            if (x < 0 || x > containerWidth || y < 0 || y > containerHeight) {
                return false
            }
        }

        return true
    }

    interface MoveViewListener {
        fun onImageSelected(image: ImageView)
    }
}
