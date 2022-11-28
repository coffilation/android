package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.coffilation.app.data.CollectionData
import com.coffilation.app.databinding.ItemDiscoveriesBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.data.ListDataSource
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PublicCollectionItem
import com.coffilation.app.view.item.PublicCollectionsListItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class PublicCollectionsListItemDelegate(
    onCollectionClick: (CollectionData) -> Unit
) : BindingAdapterDelegate<PublicCollectionsListItem, AdapterItem, ItemDiscoveriesBinding>(
    PublicCollectionsListItem::class.java,
    ItemDiscoveriesBinding::inflate
) {

    private val adapter = DataSourceAdapter(PublicCollectionItemDelegate(onCollectionClick))

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemDiscoveriesBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)
        }
    }

    override fun onBindViewHolder(
        item: PublicCollectionsListItem,
        viewHolder: BindingViewHolder<ItemDiscoveriesBinding>,
        payloads: List<Any>
    ) {
        val binding = viewHolder.binding
        binding.recyclerView.layoutManager = LinearLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        adapter.data = ListDataSource(item.collections.map { PublicCollectionItem(it) })
        adapter.notifyDataSetChanged()
    }
}
