package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemErrorBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.ErrorItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class ErrorItemDelegate(
    private val onRetryClick: (id: Int) -> Unit,
) : BindingAdapterDelegate<ErrorItem, AdapterItem, ItemErrorBinding>(
    ErrorItem::class.java,
    ItemErrorBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemErrorBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            itemView.setOnClickListener {
                onRetryClick.invoke(requireBoundData())
            }
        }
    }

    override fun onBindViewHolder(
        item: ErrorItem,
        viewHolder: BindingViewHolder<ItemErrorBinding>,
        payloads: List<Any>
    ) {
        viewHolder.setBoundData(item.id)
    }
}
