package com.coffilation.app.view.item

import androidx.annotation.CallSuper

/**
 * @author pvl-zolotov on 25.11.2022
 */
interface AdapterItem {

    fun areItemsTheSame(other: AdapterItem) = false

    fun areContentsTheSame(other: AdapterItem) = false
}

abstract class CardAdapterItem<T>(val id: T) : AdapterItem {

    @CallSuper
    override fun areItemsTheSame(other: AdapterItem): Boolean {
        return javaClass == other.javaClass && id == (other as CardAdapterItem<*>).id
    }

    override fun areContentsTheSame(other: AdapterItem) = true
}
