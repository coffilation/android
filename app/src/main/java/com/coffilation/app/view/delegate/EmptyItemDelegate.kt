package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemEmptyBinding
import com.coffilation.app.databinding.ItemLoadingBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.EmptyItem
import com.coffilation.app.view.item.LoadingItem

/**
 * @author pvl-zolotov on 29.11.2022
 */
class EmptyItemDelegate : BindingAdapterDelegate<EmptyItem, AdapterItem, ItemEmptyBinding>(
    EmptyItem::class.java,
    ItemEmptyBinding::inflate
) {

    override fun onBindViewHolder(
        item: EmptyItem,
        viewHolder: BindingViewHolder<ItemEmptyBinding>,
        payloads: List<Any>
    ) {
    }
}
