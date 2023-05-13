package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemErrorHorizontalBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.ErrorItem

/**
 * @author pvl-zolotov on 09.05.2023
 */
class ErrorHorizontalItemDelegate(
    private val onRetryClick: (id: Int) -> Unit,
) : BindingAdapterDelegate<ErrorItem, AdapterItem, ItemErrorHorizontalBinding>(
    ErrorItem::class.java,
    ItemErrorHorizontalBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemErrorHorizontalBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            itemView.setOnClickListener {
                onRetryClick.invoke(requireBoundData())
            }
        }
    }

    override fun onBindViewHolder(
        item: ErrorItem,
        viewHolder: BindingViewHolder<ItemErrorHorizontalBinding>,
        payloads: List<Any>
    ) {
        viewHolder.setBoundData(item.id)
    }
}
