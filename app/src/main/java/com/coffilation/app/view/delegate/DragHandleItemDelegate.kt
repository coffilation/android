package com.coffilation.app.view.delegate

import com.coffilation.app.databinding.ItemDragHandleBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.DragHandleItem

/**
 * @author pvl-zolotov on 29.11.2022
 */
class DragHandleItemDelegate : BindingAdapterDelegate<DragHandleItem, AdapterItem, ItemDragHandleBinding>(
    DragHandleItem::class.java,
    ItemDragHandleBinding::inflate
) {

    override fun onBindViewHolder(
        item: DragHandleItem,
        viewHolder: BindingViewHolder<ItemDragHandleBinding>,
        payloads: List<Any>
    ) {
    }
}
