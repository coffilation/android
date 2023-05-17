package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.coffilation.app.R
import com.coffilation.app.databinding.ItemSearchInputBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchInputItem

/**
 * @author pvl-zolotov on 28.11.2022
 */
class SearchInputItemDelegate(
    private val onInputChanged: (String) -> Unit,
    private val onSearchStart: () -> Unit,
    private val onBackClick: () -> Unit,
) : BindingAdapterDelegate<SearchInputItem, AdapterItem, ItemSearchInputBinding>(
    SearchInputItem::class.java,
    ItemSearchInputBinding::inflate
) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BindingViewHolder<ItemSearchInputBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.search.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchStart.invoke()
                    true
                } else {
                    false
                }
            }
            binding.search.doOnTextChanged { text, start, count, after ->
                onInputChanged.invoke(text.toString())
            }
            binding.go.setOnClickListener {
                if (!binding.search.text.isNullOrEmpty()) {
                    onSearchStart.invoke()
                } else {
                    Toast.makeText(it.context, R.string.search_query_empty, Toast.LENGTH_LONG).show()
                }
            }
            binding.back.setOnClickListener {
                onBackClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchInputItem,
        viewHolder: BindingViewHolder<ItemSearchInputBinding>,
        payloads: List<Any>
    ) {
        if (viewHolder.getBoundData<String>() != item.lastAppliedSuggestion) {
            item.lastAppliedSuggestion?.also { suggestion ->
                viewHolder.binding.search.apply {
                    viewHolder.setBoundData(suggestion)
                    setText(suggestion)
                    setSelection(suggestion.length)
                }
            }
        }
    }
}
