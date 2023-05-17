package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import com.coffilation.app.databinding.ItemSearchButtonWithNavigationBinding
import com.coffilation.app.databinding.ItemSearchInputBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchButtonWithNavigationItem
import com.coffilation.app.view.item.SearchInputItem

/**
 * @author pvl-zolotov on 28.11.2022
 */
class SearchButtonWithNavigationItemDelegate(
    private val onSearchClick: () -> Unit,
    private val onBackClick: () -> Unit,
) : BindingAdapterDelegate<SearchButtonWithNavigationItem, AdapterItem, ItemSearchButtonWithNavigationBinding>(
    SearchButtonWithNavigationItem::class.java,
    ItemSearchButtonWithNavigationBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemSearchButtonWithNavigationBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.search.setOnClickListener {
                onSearchClick.invoke()
            }
            binding.back.setOnClickListener {
                onBackClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchButtonWithNavigationItem,
        viewHolder: BindingViewHolder<ItemSearchButtonWithNavigationBinding>,
        payloads: List<Any>
    ) {
        viewHolder.binding.search.setText(item.query)
    }
}
