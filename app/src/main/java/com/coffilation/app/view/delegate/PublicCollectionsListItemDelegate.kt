package com.coffilation.app.view.delegate

import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.databinding.ItemDiscoveriesBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.util.OnEndReachedListener
import com.coffilation.app.util.delegate.BaseBindingAdapterDelegate
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PublicCollectionsListItem
import com.coffilation.app.view.viewholder.PublicCollectionListViewHolder

/**
 * @author pvl-zolotov on 26.11.2022
 */
class PublicCollectionsListItemDelegate(
    onCollectionClick: (CollectionData) -> Unit,
    onRetryClick: () -> Unit,
    autoLoadingListener: OnEndReachedListener,
) : BaseBindingAdapterDelegate<PublicCollectionsListItem, AdapterItem, ItemDiscoveriesBinding, PublicCollectionListViewHolder>(
    PublicCollectionsListItem::class.java,
    ItemDiscoveriesBinding::inflate,
    { binding -> PublicCollectionListViewHolder(binding, onCollectionClick, onRetryClick, autoLoadingListener) }
) {

    override fun onBindViewHolder(
        item: PublicCollectionsListItem,
        viewHolder: PublicCollectionListViewHolder,
        payloads: List<Any>
    ) {
        viewHolder.bind(item)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as PublicCollectionListViewHolder).unbind()
    }
}
