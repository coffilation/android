package com.coffilation.app.view.viewholder

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.databinding.ItemDiscoveriesBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.OnEndReachedListener
import com.coffilation.app.util.diffutil.AsyncListDifferDataSource
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.delegate.ErrorHorizontalItemDelegate
import com.coffilation.app.view.delegate.LoadingHorizontalItemDelegate
import com.coffilation.app.view.delegate.PublicCollectionItemDelegate
import com.coffilation.app.view.item.PublicCollectionsListItem

/**
 * @author pvl-zolotov on 12.05.2023
 */
class PublicCollectionListViewHolder(
    binding: ItemDiscoveriesBinding,
    onCollectionClick: (CollectionData) -> Unit,
    onRetryClick: () -> Unit,
    private val autoLoadingListener: OnEndReachedListener
) : BindingViewHolder<ItemDiscoveriesBinding>(binding) {

    private val adapter = DataSourceAdapter(
        PublicCollectionItemDelegate(onCollectionClick),
        ErrorHorizontalItemDelegate { onRetryClick.invoke() },
        LoadingHorizontalItemDelegate()
    )
    private var dataSource = AsyncListDifferDataSource.createFor(adapter)

    init {
        with(binding.recyclerView) {
            adapter = this@PublicCollectionListViewHolder.adapter.apply {
                data = dataSource
            }
            binding.recyclerView.addOnScrollListener(autoLoadingListener)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                binding.root.context,
                RecyclerView.HORIZONTAL,
                false
            )
        }
    }

    fun bind(item: PublicCollectionsListItem) {
        if (getBoundData<PublicCollectionsListItem>() != item) {
            setBoundData(item)
            dataSource.setItems(item.items) {
                autoLoadingListener.reset(item.autoLoadingEnabled)
                binding.recyclerView.addOnLayoutChangeListener(autoLoadingListener)
            }
        }
    }

    fun unbind() {
        binding.recyclerView.removeOnScrollListener(autoLoadingListener)
    }
}
