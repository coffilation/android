package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.coffilation.app.R
import com.coffilation.app.data.CollectionData
import com.coffilation.app.databinding.ItemUserCollectionBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.toDrawable
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.UserCollectionItem

/**
 * @author pvl-zolotov on 27.11.2022
 */
class UserCollectionItemDelegate(
    private val onCollectionClick: (CollectionData) -> Unit
) : BindingAdapterDelegate<UserCollectionItem, AdapterItem, ItemUserCollectionBinding>(
    UserCollectionItem::class.java,
    ItemUserCollectionBinding::inflate
) {

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemUserCollectionBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.root.setOnClickListener {
                onCollectionClick.invoke(requireBoundData())
            }
        }
    }

    override fun onBindViewHolder(
        item: UserCollectionItem,
        viewHolder: BindingViewHolder<ItemUserCollectionBinding>,
        payloads: List<Any>
    ) {
        viewHolder.setBoundData(item.collection)
        viewHolder.binding.title.text = item.collection.name
        val description = item.collection.description
        viewHolder.binding.description.isVisible = description?.isNotEmpty() == true
        viewHolder.binding.description.text = description
        viewHolder.binding.collectionPic.text = item.collection.name.first().toString()
        viewHolder.binding.cardBackground.background =
            item.collection.gradient?.toDrawable() ?:
                ResourcesCompat.getDrawable(viewHolder.itemView.resources, R.drawable.gradient_yellow_shape, null)
    }
}
