package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemLoadingHorizontalBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.LoadingItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class LoadingHorizontalItemDelegate : BindingAdapterDelegate<LoadingItem, AdapterItem, ItemLoadingHorizontalBinding>(
    LoadingItem::class.java,
    ItemLoadingHorizontalBinding::inflate
) {

    override fun onBindViewHolder(
        item: LoadingItem,
        viewHolder: BindingViewHolder<ItemLoadingHorizontalBinding>,
        payloads: List<Any>
    ) {
    }
}
