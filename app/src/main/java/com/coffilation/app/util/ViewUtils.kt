package com.coffilation.app.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.coffilation.app.R
import com.coffilation.app.models.GradientData
import okhttp3.internal.toHexString

/**
 * @author pvl-zolotov on 27.11.2022
 */
fun GradientData?.toDrawable(resources: Resources): Drawable {
    return if (this != null) {
        GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor),
            )
        )
    } else {
        requireNotNull(ResourcesCompat.getDrawable(resources, R.drawable.gradient_yellow_shape, null))
    }
}

fun @receiver:ColorInt Int.toColorHexString(): String {
    return "#${toHexString().substring(2)}"
}

fun hideKeyboard(view: View) {
    view.clearFocus()
    val imm = view.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun hideKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        hideKeyboard(view)
    }
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
