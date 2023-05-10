package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.databinding.ItemDiscoveriesBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.OnBottomReachedListener
import com.coffilation.app.util.data.ListDataSource
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PublicCollectionsListItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class PublicCollectionsListItemDelegate(
    onCollectionClick: (CollectionData) -> Unit,
    onListEndReached: () -> Unit
) : BindingAdapterDelegate<PublicCollectionsListItem, AdapterItem, ItemDiscoveriesBinding>(
    PublicCollectionsListItem::class.java,
    ItemDiscoveriesBinding::inflate
) {

    private var autoLoadingListener = OnBottomReachedListener(3, onListEndReached)

    private val adapter = DataSourceAdapter(
        PublicCollectionItemDelegate(onCollectionClick),
        ErrorHorizontalItemDelegate(),
        LoadingHorizontalItemDelegate()
    )

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemDiscoveriesBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.addOnScrollListener(autoLoadingListener)
            binding.recyclerView.layoutManager = LinearLayoutManager(
                binding.root.context,
                RecyclerView.HORIZONTAL,
                false
            )
        }
    }

    override fun onBindViewHolder(
        item: PublicCollectionsListItem,
        viewHolder: BindingViewHolder<ItemDiscoveriesBinding>,
        payloads: List<Any>
    ) {
        val binding = viewHolder.binding
        adapter.data = ListDataSource(item.items)
        adapter.notifyDataSetChanged()
        autoLoadingListener.reset(item.autoLoadingEnabled)
        binding.recyclerView.addOnLayoutChangeListener(autoLoadingListener)
    }
}
