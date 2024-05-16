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
    var frameImageY: Float = 0f,
    var imageScaleX: Float = 1f,
    var imageScaleY: Float = 1f,
    var imageRotation: Float = 0f,
    var secondImageX: Float = 0f,
    var secondImageY: Float = 0f,
    var secondImageScaleX: Float = 1f,
    var secondImageScaleY: Float = 1f,
    var secondImageRotation: Float = 0f,
)