package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemErrorBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.ErrorItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class ErrorItemDelegate : BindingAdapterDelegate<ErrorItem, AdapterItem, ItemErrorBinding>(
    ErrorItem::class.java,
    ItemErrorBinding::inflate
) {

    override fun onBindViewHolder(
        item: ErrorItem,
        viewHolder: BindingViewHolder<ItemErrorBinding>,
        payloads: List<Any>
    ) {
    }
}
