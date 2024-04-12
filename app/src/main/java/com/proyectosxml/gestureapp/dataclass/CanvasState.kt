package com.proyectosxml.gestureapp.dataclass

data class CanvasState(
    var imageX: Float = 10f,
    var imageY: Float = 10f,
    var imageRotation: Float = 0f,
    var imageScaleX: Float = 1f,
    var imageScaleY: Float = 1f,
    var height: Int = 300,
    var width: Int = 300
)
