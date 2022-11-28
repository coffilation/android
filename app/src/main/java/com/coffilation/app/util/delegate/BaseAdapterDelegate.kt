package com.coffilation.app.util.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.util.data.AdapterDataSource

/**
 * @author pvl-zolotov on 26.11.2022
 */
abstract class BaseAdapterDelegate<in ItemType : BaseItemType, BaseItemType, VH : RecyclerView.ViewHolder> :
    AdapterDelegate<AdapterDataSource<BaseItemType>>() {

    override fun isApplicableForPosition(
        dataSource: AdapterDataSource<BaseItemType>,
        position: Int
    ): Boolean {
        return isApplicableForItem(dataSource.getItem(position))
    }

    override fun onBindViewHolder(
        dataSource: AdapterDataSource<BaseItemType>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        @Suppress("UNCHECKED_CAST")
        onBindViewHolder(dataSource.getItem(position) as ItemType, holder as VH, payloads)
    }

    override fun getItemId(dataSource: AdapterDataSource<BaseItemType>, position: Int): Int {
        @Suppress("UNCHECKED_CAST")
        return getItemId(dataSource.getItem(position) as ItemType)
    }

    abstract override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): VH

    protected abstract fun isApplicableForItem(item: BaseItemType): Boolean

    protected abstract fun onBindViewHolder(
        item: ItemType,
        viewHolder: VH,
        payloads: List<Any>
    )

    protected open fun getItemId(item: ItemType): Int = -1
}
