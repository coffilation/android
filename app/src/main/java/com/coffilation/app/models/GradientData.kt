package com.coffilation.app.models

import android.content.Context
import androidx.core.content.ContextCompat
import com.coffilation.app.R
import com.coffilation.app.util.toColorHexString

/**
 * @author pvl-zolotov on 21.11.2022
 */
data class GradientData(
    val startColor: String,
    val endColor: String,
) {

    companion object {

        fun getRed(context: Context) = GradientData(
            ContextCompat.getColor(context, R.color.gradient_red_start).toColorHexString(),
            ContextCompat.getColor(context, R.color.gradient_red_end).toColorHexString()
        )

        fun getYellow(context: Context) = GradientData(
            ContextCompat.getColor(context, R.color.gradient_yellow_start).toColorHexString(),
            ContextCompat.getColor(context, R.color.gradient_yellow_end).toColorHexString()
        )

        fun getGreen(context: Context) = GradientData(
            ContextCompat.getColor(context, R.color.gradient_green_start).toColorHexString(),
            ContextCompat.getColor(context, R.color.gradient_green_end).toColorHexString()
        )

        fun getBlue(context: Context) = GradientData(
            ContextCompat.getColor(context, R.color.gradient_blue_start).toColorHexString(),
            ContextCompat.getColor(context, R.color.gradient_blue_end).toColorHexString()
        )

        fun getViolet(context: Context) = GradientData(
            ContextCompat.getColor(context, R.color.gradient_violet_start).toColorHexString(),
            ContextCompat.getColor(context, R.color.gradient_violet_end).toColorHexString()
        )
    }
}
