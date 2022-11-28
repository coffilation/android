package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemUserCollectionsHeaderBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.UserCollectionsHeaderItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class UserCollectionsHeaderItemDelegate(
    private val onCollectionAddClick: () -> Unit
) : BindingAdapterDelegate<UserCollectionsHeaderItem, AdapterItem, ItemUserCollectionsHeaderBinding>(
    UserCollectionsHeaderItem::class.java,
    ItemUserCollectionsHeaderBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemUserCollectionsHeaderBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.add.setOnClickListener {
                onCollectionAddClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(
        item: UserCollectionsHeaderItem,
        viewHolder: BindingViewHolder<ItemUserCollectionsHeaderBinding>,
        payloads: List<Any>
    ) {
    }
}
