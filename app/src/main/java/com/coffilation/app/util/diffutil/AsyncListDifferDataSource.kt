package com.coffilation.app.util.diffutil

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.data.AdapterDataSource
import com.coffilation.app.view.item.AdapterItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class AsyncListDifferDataSource<T : AdapterItem> private constructor(
    private val differ: AsyncListDiffer<T>
) : AdapterDataSource<T> {

    var items: List<T>
        get() = differ.currentList
        set(value) {
            setItems(value)
        }

    fun setItems(items: List<T>, commitCallback: (() -> Unit)? = null) {
        differ.submitList(items.takeIf { it.isNotEmpty() }, commitCallback)
    }

    override fun getItem(position: Int): T = differ.currentList[position]

    override fun getItemCount() = differ.currentList.size

    companion object {

        @JvmStatic
        fun <T : AdapterItem> createFor(
            adapter: DataSourceAdapter<T>
        ): AsyncListDifferDataSource<T> {
            return AsyncListDifferDataSource(
                AsyncListDiffer<T>(
                    adapter,
                    object : DiffUtil.ItemCallback<T>() {

                        override fun areItemsTheSame(p0: T, p1: T): Boolean {
                            return p0.areItemsTheSame(p1)
                        }

                        override fun areContentsTheSame(p0: T, p1: T): Boolean {
                            return p0.areContentsTheSame(p1)
                        }
                    }
                )
            )
        }
    }
}

