package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import com.coffilation.app.databinding.ItemPointCollectionBinding
import com.coffilation.app.models.CollectionPointData
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.toDrawable
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.PointCollectionItem

/**
 * @author pvl-zolotov on 14.05.2023
 */
class PointCollectionItemDelegate(
    private val onCollectionCheckedChange: (CollectionPointData) -> Unit
) : BindingAdapterDelegate<PointCollectionItem, AdapterItem, ItemPointCollectionBinding>(
    PointCollectionItem::class.java,
    ItemPointCollectionBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemPointCollectionBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.root.setOnClickListener {
                val collectionPointData = requireBoundData<CollectionPointData>()
                collectionPointData.isPlaceIncluded = !requireBoundData<CollectionPointData>().isPlaceIncluded
                onCollectionCheckedChange.invoke(collectionPointData)
            }
        }
    }

    override fun onBindViewHolder(
        item: PointCollectionItem,
        viewHolder: BindingViewHolder<ItemPointCollectionBinding>,
        payloads: List<Any>
    ) {
        val collection = item.collection
        viewHolder.setBoundData(collection)
        viewHolder.binding.title.text = collection.name
        viewHolder.binding.collectionPic.text = collection.name.first().toString()
        val gradientData = collection.getGradientData()
        viewHolder.binding.cardBackground.background = gradientData.toDrawable(viewHolder.itemView.resources)
        viewHolder.binding.checkbox.isChecked = collection.isPlaceIncluded
        viewHolder.binding.checkbox.isEnabled = collection.canChangePlacesList
        viewHolder.binding.root.isClickable = collection.canChangePlacesList
        viewHolder.binding.root.isFocusable = collection.canChangePlacesList
    }
}
