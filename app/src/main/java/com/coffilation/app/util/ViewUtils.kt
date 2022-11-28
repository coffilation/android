package com.coffilation.app.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
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

fun dpToPx(context: Context, dp: Float): Int {
    return dpToPxF(dp, context.resources).toInt()
}

fun dpToPx(dp: Float, resources: Resources): Int {
    return dpToPxF(dp, resources).toInt()
}

fun dpToPxF(dp: Float, resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun spToPxF(sp: Float, resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun pxToDp(px: Float, resources: Resources): Int {
    return pxToDpF(px, resources).toInt()
}

fun pxToDpF(px: Float, resources: Resources): Float {
    return px / resources.displayMetrics.density
}
