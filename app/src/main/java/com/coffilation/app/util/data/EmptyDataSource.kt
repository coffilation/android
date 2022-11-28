package com.coffilation.app.util.data

/**
 * @author pvl-zolotov on 26.11.2022
 */
class EmptyDataSource<T> : AdapterDataSource<T> {

    override fun getItem(position: Int) = throw UnsupportedOperationException("Method is not supported")

    override fun getItemCount() = 0
}
