package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemSearchButtonBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchButtonItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class SearchButtonItemDelegate(
    private val onSearchClick: () -> Unit,
    private val onUserClick: () -> Unit
) : BindingAdapterDelegate<SearchButtonItem, AdapterItem, ItemSearchButtonBinding>(
    SearchButtonItem::class.java,
    ItemSearchButtonBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemSearchButtonBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.search.setOnClickListener {
                onSearchClick.invoke()
            }
            binding.userPic.setOnClickListener {
                onUserClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchButtonItem,
        viewHolder: BindingViewHolder<ItemSearchButtonBinding>,
        payloads: List<Any>
    ) {
        viewHolder.binding.userLetter.text = item.username.firstOrNull()?.toString() ?: "?"
    }
}
