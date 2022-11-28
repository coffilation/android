package com.coffilation.app.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.util.delegate.AdapterDelegate

/**
 * @author pvl-zolotov on 26.11.2022
 */
abstract class BaseAdapter<DataSource : Any>(
    var data: DataSource,
    private val delegates: List<AdapterDelegate<DataSource>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var inflater: LayoutInflater? = null

    fun getDelegateViewType(delegate: AdapterDelegate<DataSource>): Int {
        return delegates.indexOf(delegate)
    }

    override fun getItemId(position: Int): Long {
        val viewType = getItemViewType(position)
        val id = getDelegateForViewType(viewType).getItemId(data, position)
        return (viewType.toLong() shl 32) or (id.toLong() and 0xFFFFFFFF)
    }

    override fun getItemViewType(position: Int): Int {
        delegates.forEachIndexed { index, delegate ->
            if (delegate.isApplicableForPosition(data, position)) {
                return index
            }
        }
        throw IllegalStateException(
            "No AdapterDelegate added that matches position=$position in data source"
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getDelegateForViewType(viewType)
            .onCreateViewHolder(getLayoutInflater(parent), parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, PAYLOADS_EMPTY_LIST)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        getDelegateForViewType(holder.itemViewType)
            .onBindViewHolder(data, position, holder, payloads)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getDelegateForViewType(holder.itemViewType).onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return getDelegateForViewType(holder.itemViewType).onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        getDelegateForViewType(holder.itemViewType).onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getDelegateForViewType(holder.itemViewType).onViewDetachedFromWindow(holder)
    }

    private fun getDelegateForViewType(viewType: Int): AdapterDelegate<DataSource> {
        return delegates[viewType]
    }

    private fun getLayoutInflater(view: View): LayoutInflater {
        inflater?.let { return it }
        val newInflater = LayoutInflater.from(view.context)
        inflater = newInflater
        return newInflater
    }

    companion object {

        private val PAYLOADS_EMPTY_LIST = emptyList<Any>()
    }
}
