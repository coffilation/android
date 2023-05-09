package com.coffilation.app.models

import androidx.annotation.ColorInt
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

/**
 * @author pvl-zolotov on 21.11.2022
 */
data class ColorData(
    val red: Int,
    val green: Int,
    val blue: Int,
) {

    companion object {

        fun newInstance(@ColorInt color: Int): ColorData {
            return ColorData(red = color.red, green = color.green, blue = color.blue)
        }
    }
}
