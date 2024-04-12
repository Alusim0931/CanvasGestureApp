package com.proyectosxml.gestureapp.dataclass

data class ImageState(
    var imageAppeared: Boolean = false,
    var finalImageX: Float = 0f,
    var finalImageY: Float = 0f,
    var savedImageX: Float = 0f,
    var savedImageY: Float = 0f,
    var isGestureInProgress: Boolean = false,
    var currentGesture: GestureState = GestureState.NONE,
    var frameImageX: Float = 0f,
    var frameImageY: Float = 0f
)