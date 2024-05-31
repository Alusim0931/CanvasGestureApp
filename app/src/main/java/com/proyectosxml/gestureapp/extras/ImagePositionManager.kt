package com.proyectosxml.gestureapp.extras

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.graphics.PointF
import android.widget.FrameLayout
import android.widget.ImageView

class ImagePositionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveImagePosition(imageView: ImageView, keyX: String?, keyY: String?) {
        sharedPreferences.edit()
            .putString(keyX, imageView.x.toString())
            .putString(keyY, imageView.y.toString())
            .apply()
    }

    fun getImagePosition(keyX: String?, keyY: String?): PointF {
        val x = sharedPreferences.getString(keyX, "0")?.toFloatOrNull() ?: 0f
        val y = sharedPreferences.getString(keyY, "0")?.toFloatOrNull() ?: 0f
        return PointF(x, y)
    }

    companion object {
        private const val PREFS_NAME = "ImagePositions"
        const val KEY_IMAGE1_X = "image1_x"
        const val KEY_IMAGE1_Y = "image1_y"
        const val KEY_IMAGE2_X = "image2_x"
        const val KEY_IMAGE2_Y = "image2_y"
    }

    fun restoreImagePositions(imageView1: ImageView, imageView2: ImageView) {
        val image1Position = getImagePosition(KEY_IMAGE1_X, KEY_IMAGE1_Y)
        val image2Position = getImagePosition(KEY_IMAGE2_X, KEY_IMAGE2_Y)

        imageView1.x = image1Position.x
        imageView1.y = image1Position.y

        imageView2.x = image2Position.x
        imageView2.y = image2Position.y
    }
}