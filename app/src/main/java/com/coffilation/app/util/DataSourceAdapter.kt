package com.coffilation.app.util

import com.coffilation.app.util.delegate.AdapterDelegate
import com.coffilation.app.util.data.AdapterDataSource
import com.coffilation.app.util.data.EmptyDataSource

/**
 * @author pvl-zolotov on 26.11.2022
 */
class DataSourceAdapter<T> private constructor(
    delegates: List<AdapterDelegate<AdapterDataSource<T>>>
) : BaseAdapter<AdapterDataSource<T>>(
    EmptyDataSource(),
    delegates
) {

    constructor(vararg delegates: AdapterDelegate<AdapterDataSource<T>>) : this(delegates.asList())

    override fun getItemCount() = data.getItemCount()

    class Builder<T> {

        private val delegates = arrayListOf<AdapterDelegate<AdapterDataSource<T>>>()

        fun <D : AdapterDelegate<AdapterDataSource<T>>> add(delegate: D): Builder<T> {
            delegates.add(delegate)
            return this
        }

        fun build() = DataSourceAdapter(delegates)
    }
}

