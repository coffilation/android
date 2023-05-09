package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemErrorHorizontalBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.ErrorItem

/**
 * @author pvl-zolotov on 09.05.2023
 */
class ErrorHorizontalItemDelegate : BindingAdapterDelegate<ErrorItem, AdapterItem, ItemErrorHorizontalBinding>(
    ErrorItem::class.java,
    ItemErrorHorizontalBinding::inflate
) {

    override fun onBindViewHolder(
        item: ErrorItem,
        viewHolder: BindingViewHolder<ItemErrorHorizontalBinding>,
        payloads: List<Any>
    ) {
    }
}
