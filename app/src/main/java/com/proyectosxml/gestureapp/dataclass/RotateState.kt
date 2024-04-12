package com.proyectosxml.gestureapp.dataclass

data class RotateGestureState(
    var prevX1: Float = 0f,
    var prevY1: Float = 0f,
    var prevX2: Float = 0f,
    var prevY2: Float = 0f,
    var initialAngle: Double = 0.0,
    var accumulatedAngle: Float = 0f,
    val rotationThreshold: Float = 10f,
    val rotationScaleFactor: Float = 0.05f
)