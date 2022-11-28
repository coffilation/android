package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemLoadingBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.LoadingItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class LoadingItemDelegate : BindingAdapterDelegate<LoadingItem, AdapterItem, ItemLoadingBinding>(
    LoadingItem::class.java,
    ItemLoadingBinding::inflate
) {

    override fun onBindViewHolder(
        item: LoadingItem,
        viewHolder: BindingViewHolder<ItemLoadingBinding>,
        payloads: List<Any>
    ) {
    }
}
