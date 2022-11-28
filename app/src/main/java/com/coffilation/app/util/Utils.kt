package com.coffilation.app.util

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.coffilation.app.data.GradientData

/**
 * @author pvl-zolotov on 27.11.2022
 */
fun GradientData.toDrawable(): Drawable {
    return GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        intArrayOf(
            Color.rgb(startColor.red, startColor.green, startColor.blue),
            Color.rgb(endColor.red, endColor.green, endColor.blue),
        )
    )
}

fun <T : Any> Fragment.setBackStackData(key: String, data: T, goBack: Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (goBack) {
        findNavController().popBackStack()
    }
}

fun <T : Any> Fragment.getBackStackData(key: String): T? {
    return findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)
}
