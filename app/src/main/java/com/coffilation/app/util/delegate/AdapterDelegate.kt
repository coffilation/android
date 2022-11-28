package com.coffilation.app.util.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author pvl-zolotov on 26.11.2022
 */
abstract class AdapterDelegate<DataSource> {

    abstract fun isApplicableForPosition(dataSource: DataSource, position: Int): Boolean

    abstract fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RecyclerView.ViewHolder

    abstract fun onBindViewHolder(
        dataSource: DataSource,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    )

    open fun onViewRecycled(holder: RecyclerView.ViewHolder) {}

    open fun onFailedToRecycleView(holder: RecyclerView.ViewHolder) = false

    open fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {}

    open fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {}

    open fun getItemId(dataSource: DataSource, position: Int): Int = -1
}
