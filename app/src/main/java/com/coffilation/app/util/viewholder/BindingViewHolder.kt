package com.coffilation.app.util.viewholder

import androidx.viewbinding.ViewBinding

/**
 * @author pvl-zolotov on 26.11.2022
 */
open class BindingViewHolder<T : ViewBinding>(
    val binding: T,
    private val reusableCondition: (MutableList<Any>) -> Boolean = { false }
) : ReusableViewHolder(binding.root) {

    private var boundData: Any? = null

    override fun canReuseUpdatedViewHolder(payloads: MutableList<Any>): Boolean {
        return reusableCondition.invoke(payloads)
    }

    fun <T> setBoundData(data: T) {
        boundData = data
    }

    fun <T> getBoundData(): T? {
        @Suppress("UNCHECKED_CAST")
        return boundData as T?
    }

    fun <T> requireBoundData(): T {
        return checkNotNull(getBoundData())
    }
}

