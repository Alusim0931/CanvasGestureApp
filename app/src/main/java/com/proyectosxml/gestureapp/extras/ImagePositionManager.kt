package com.proyectosxml.gestureapp.extras

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.widget.FrameLayout
import android.widget.ImageView

class ImagePositionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveImagePosition(imageView: ImageView, keyX: String?, keyY: String?) {
        val params = imageView.layoutParams as FrameLayout.LayoutParams
        sharedPreferences.edit()
            .putInt(keyX, params.leftMargin)
            .putInt(keyY, params.topMargin)
            .apply()
    }

    fun getImagePosition(keyX: String?, keyY: String?): Point {
        val x = sharedPreferences.getInt(keyX, 0)
        val y = sharedPreferences.getInt(keyY, 0)
        return Point(x, y)
    }

    companion object {
        private const val PREFS_NAME = "ImagePositions"
        const val KEY_IMAGE1_X = "image1_x"
        const val KEY_IMAGE1_Y = "image1_y"
        const val KEY_IMAGE2_X = "image2_x"
        const val KEY_IMAGE2_Y = "image2_y"
    }

    fun restoreImagePositions(imageView1: ImageView, imageView2: ImageView, context: Context) {
        val positionManager = ImagePositionManager(context)

        val image1Position = positionManager.getImagePosition(KEY_IMAGE1_X, KEY_IMAGE1_Y)
        val image2Position = positionManager.getImagePosition(KEY_IMAGE2_X, KEY_IMAGE2_Y)

        val params1 = imageView1.layoutParams as FrameLayout.LayoutParams
        params1.leftMargin = image1Position.x
        params1.topMargin = image1Position.y
        imageView1.layoutParams = params1

        val params2 = imageView2.layoutParams as FrameLayout.LayoutParams
        params2.leftMargin = image2Position.x
        params2.topMargin = image2Position.y
        imageView2.layoutParams = params2
    }
}