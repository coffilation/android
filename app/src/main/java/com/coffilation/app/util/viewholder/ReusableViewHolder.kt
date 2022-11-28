package com.coffilation.app.util.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author pvl-zolotov on 26.11.2022
 */
abstract class ReusableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun canReuseUpdatedViewHolder(payloads: MutableList<Any>): Boolean {
        return true
    }
}
