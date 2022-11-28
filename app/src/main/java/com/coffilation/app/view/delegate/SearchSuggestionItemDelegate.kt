package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.coffilation.app.R
import com.coffilation.app.data.CollectionData
import com.coffilation.app.databinding.ItemDiscoveryBinding
import com.coffilation.app.databinding.ItemSearchSuggestionBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.toDrawable
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PublicCollectionItem
import com.coffilation.app.view.item.SearchSuggestionItem
import com.yandex.mapkit.search.SuggestItem

/**
 * @author pvl-zolotov on 28.11.2022
 */
class SearchSuggestionItemDelegate(
    private val onSuggestionClick: (SuggestItem) -> Unit
) : BindingAdapterDelegate<SearchSuggestionItem, AdapterItem, ItemSearchSuggestionBinding>(
    SearchSuggestionItem::class.java,
    ItemSearchSuggestionBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemSearchSuggestionBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.root.setOnClickListener {
                onSuggestionClick.invoke(requireBoundData())
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchSuggestionItem,
        viewHolder: BindingViewHolder<ItemSearchSuggestionBinding>,
        payloads: List<Any>
    ) {
        viewHolder.setBoundData(item.suggestion)
        viewHolder.binding.name.text = item.suggestion.displayText
    }
}
