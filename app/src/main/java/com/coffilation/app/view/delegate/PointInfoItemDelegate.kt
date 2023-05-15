package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemPointInfoBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PointInfoItem

/**
 * @author pvl-zolotov on 14.05.2023
 */
class PointInfoItemDelegate(
    //private val onBackClick: () -> Unit,
) : BindingAdapterDelegate<PointInfoItem, AdapterItem, ItemPointInfoBinding>(
    PointInfoItem::class.java,
    ItemPointInfoBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemPointInfoBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            /*binding.back.setOnClickListener {
                onBackClick.invoke()
            }*/
        }
    }

    override fun onBindViewHolder(
        item: PointInfoItem,
        viewHolder: BindingViewHolder<ItemPointInfoBinding>,
        payloads: List<Any>
    ) {
        val point = item.pointData
        viewHolder.binding.title.text = point.getName()
        viewHolder.binding.address.text = point.getAddress()
    }
}

