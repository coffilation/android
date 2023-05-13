package com.coffilation.app.util

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author pvl-zolotov on 10.05.2023
 */
class OnEndReachedListener(
    private val offset: Int,
    private val action: () -> Unit
) : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

    private var invokeTimestamp = 0L
    private var enabled = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        tryInvokeOnAnimationFinished(recyclerView)
    }

    override fun onLayoutChange(
        v: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        v.removeOnLayoutChangeListener(this)
        tryInvokeOnAnimationFinished(v as RecyclerView)
    }

    fun reset(enable: Boolean) {
        invokeTimestamp = 0
        enabled = enable
    }

    fun tryInvokeOnAnimationFinished(recyclerView: RecyclerView) {
        if (enabled) {
            recyclerView.itemAnimator?.isRunning {
                if (enabled) {
                    invokeIfDebounced(recyclerView)
                }
            } ?: invokeIfDebounced(recyclerView)
        }
    }

    private fun invokeIfDebounced(recyclerView: RecyclerView) {
        if (System.currentTimeMillis() - invokeTimestamp >= 200L) {
            invokeOnBottomReached(recyclerView)
        }
    }

    private fun invokeOnBottomReached(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        if (totalItemCount - lastVisibleItem <= offset) {
            invokeTimestamp = System.currentTimeMillis()
            action.invoke()
        }
    }
}
