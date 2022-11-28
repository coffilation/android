package com.coffilation.app.util.data

/**
 * @author pvl-zolotov on 27.11.2022
 */
class ListDataSource<T>(val items: List<T> = emptyList()) : AdapterDataSource<T> {

    override fun getItem(position: Int) = items[position]

    override fun getItemCount() = items.size
}

