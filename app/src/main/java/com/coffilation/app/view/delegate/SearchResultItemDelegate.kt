package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.data.PointData
import com.coffilation.app.databinding.ItemSearchResultBinding
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
        viewHolder.setBoundData(item.point)
        viewHolder.binding.name.text = item.point.name
        viewHolder.binding.address.text = item.point.displayName.removePrefix("${item.point.name}, ")
    }
}
