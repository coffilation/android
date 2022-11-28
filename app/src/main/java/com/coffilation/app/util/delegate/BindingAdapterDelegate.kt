package com.coffilation.app.util.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.coffilation.app.util.viewholder.BindingViewHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author pvl-zolotov on 26.11.2022
 */
abstract class BaseBindingAdapterDelegate<in ItemType : BaseItemType, BaseItemType,
    VB : ViewBinding, VH : BindingViewHolder<VB>>(
    private val itemClass: Class<ItemType>,
    private val viewBindingBasedInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    private val viewHolderCreator: (VB) -> VH
) : BaseAdapterDelegate<ItemType, BaseItemType, VH>() {

    @CallSuper
    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): VH {
        val binding = viewBindingBasedInflater.invoke(inflater, parent, false)
        return viewHolderCreator.invoke(binding)
    }

    @CallSuper
    override fun isApplicableForItem(item: BaseItemType): Boolean {
        return itemClass.isInstance(item)
    }

    fun <T> boundData(): ReadWriteProperty<BindingViewHolder<*>, T?> {
        return BoundDataProperty()
    }

    private class BoundDataProperty<T> : ReadWriteProperty<BindingViewHolder<*>, T?> {

        override fun getValue(thisRef: BindingViewHolder<*>, property: KProperty<*>): T? {
            return thisRef.getBoundData()
        }

        override fun setValue(thisRef: BindingViewHolder<*>, property: KProperty<*>, value: T?) {
            thisRef.setBoundData(value)
        }
    }
}

abstract class BindingAdapterDelegate<in ItemType : BaseItemType, BaseItemType, VB : ViewBinding>(
    itemClass: Class<ItemType>,
    viewBindingBasedInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    reusableCondition: (MutableList<Any>) -> Boolean = { false }
) : BaseBindingAdapterDelegate<ItemType, BaseItemType, VB, BindingViewHolder<VB>>(
    itemClass,
    viewBindingBasedInflater,
    { binding -> BindingViewHolder(binding, reusableCondition) }
)
