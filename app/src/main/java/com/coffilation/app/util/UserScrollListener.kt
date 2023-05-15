package com.coffilation.app.util

import androidx.recyclerview.widget.RecyclerView

/**
 * @author pvl-zolotov on 15.05.2023
 */
class UserScrollListener(private val onScrolled: () -> Unit) : RecyclerView.OnScrollListener() {

    private var isScrolledByUser = false

    override fun onScrollStateChanged(
        recyclerView: RecyclerView,
        newState: Int
    ) {
        when (newState) {
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                isScrolledByUser = true
            }
            RecyclerView.SCROLL_STATE_IDLE -> {
                if (isScrolledByUser) {
                    onScrolled.invoke()
                }
                isScrolledByUser = false
            }
        }
    }
}
