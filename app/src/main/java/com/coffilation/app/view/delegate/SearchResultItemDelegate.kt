package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemSearchResultBinding
import com.coffilation.app.models.PointData
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchResultItem

/**
 * @author pvl-zolotov on 27.11.2022
 */
class SearchResultItemDelegate(
    private val onPointClick: (PointData) -> Unit
) : BindingAdapterDelegate<SearchResultItem, AdapterItem, ItemSearchResultBinding>(
    SearchResultItem::class.java,
    ItemSearchResultBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemSearchResultBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.root.apply {
                setOnClickListener {
                    onPointClick.invoke(requireBoundData())
                }
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchResultItem,
        viewHolder: BindingViewHolder<ItemSearchResultBinding>,
        payloads: List<Any>
    ) {
        val point = item.point
        viewHolder.setBoundData(point)
        viewHolder.binding.name.text = point.getName()
        viewHolder.binding.address.text = point.getAddress()
    }
}
