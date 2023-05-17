package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.coffilation.app.databinding.ItemCollectionInfoBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.toDrawable
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.CollectionInfoItem

/**
 * @author pvl-zolotov on 16.05.2023
 */
class CollectionInfoItemDelegate(
    private val onEditCollectionClick: (CollectionData) -> Unit,
    private val onRemoveCollectionClick: (Long) -> Unit,
    private val onBackClick: () -> Unit,
) : BindingAdapterDelegate<CollectionInfoItem, AdapterItem, ItemCollectionInfoBinding>(
    CollectionInfoItem::class.java,
    ItemCollectionInfoBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemCollectionInfoBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.editButton.setOnClickListener {
                val collectionData = requireBoundData<CollectionData>()
                onEditCollectionClick.invoke(collectionData)
            }
            binding.deleteButton.setOnClickListener {
                val collectionData = requireBoundData<CollectionData>()
                onRemoveCollectionClick.invoke(collectionData.id)
            }
            binding.back.setOnClickListener {
                onBackClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(
        item: CollectionInfoItem,
        viewHolder: BindingViewHolder<ItemCollectionInfoBinding>,
        payloads: List<Any>
    ) {
        val collection = item.collection
        viewHolder.setBoundData(collection)
        viewHolder.binding.title.text = collection.name
        val description = collection.description
        viewHolder.binding.description.isVisible = description?.isNotEmpty() == true
        viewHolder.binding.description.text = description
        viewHolder.binding.collectionPic.text = collection.name.first().toString()
        val gradientData = collection.getGradientData()
        viewHolder.binding.cardBackground.background = gradientData.toDrawable(viewHolder.itemView.resources)
    }
}
