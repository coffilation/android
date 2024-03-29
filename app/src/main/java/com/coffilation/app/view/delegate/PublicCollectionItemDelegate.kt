package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.coffilation.app.R
import com.coffilation.app.models.CollectionData
import com.coffilation.app.databinding.ItemDiscoveryBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.toDrawable
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PublicCollectionItem

/**
 * @author pvl-zolotov on 27.11.2022
 */
class PublicCollectionItemDelegate(
    private val onCollectionClick: (CollectionData) -> Unit
) : BindingAdapterDelegate<PublicCollectionItem, AdapterItem, ItemDiscoveryBinding>(
    PublicCollectionItem::class.java,
    ItemDiscoveryBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemDiscoveryBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.root.setOnClickListener {
                onCollectionClick.invoke(requireBoundData())
            }
        }
    }

    override fun onBindViewHolder(
        item: PublicCollectionItem,
        viewHolder: BindingViewHolder<ItemDiscoveryBinding>,
        payloads: List<Any>
    ) {
        val collection = item.collection
        viewHolder.setBoundData(collection)
        viewHolder.binding.title.text = collection.name
        val gradientData = collection.getGradientData()
        viewHolder.binding.cardBackground.background = gradientData.toDrawable(viewHolder.itemView.resources)
    }
}
