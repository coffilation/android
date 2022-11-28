package com.coffilation.app.util.data

/**
 * @author pvl-zolotov on 26.11.2022
 */
interface AdapterDataSource<out T> {
    fun getItem(position: Int): T
    fun getItemCount(): Int
}
