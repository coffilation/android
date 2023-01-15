package com.coffilation.app.util

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author pvl-zolotov on 29.11.2022
 */
class RatioLayoutManager constructor(
    context: Context?,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    private val ratio: Float = 0.8f
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = width - paddingTop - paddingBottom

    override fun generateDefaultLayoutParams() =
        scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
        scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
        scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) =
        layoutParams.apply {
            when (orientation) {
                RecyclerView.HORIZONTAL -> width = (horizontalSpace * ratio + 0.5).toInt()
                RecyclerView.VERTICAL -> height = (verticalSpace * ratio + 0.5).toInt()
            }
        }
}
