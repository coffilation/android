package com.coffilation.app.domain

import com.coffilation.app.models.BasicList

/**
 * @author pvl-zolotov on 11.05.2023
 */
sealed interface BasicState<T> {

    class Initial<T> : BasicState<T>

    class Loading<T>(
        val currentPage: Int = 0,
        val data: List<T> = emptyList(),
    ) : BasicState<T>

    class LoadingError<T>(
        val currentPage: Int,
        val error: Throwable,
        val data: List<T> = emptyList(),
    ) : BasicState<T>

    class CanLoadMore<T>(
        val currentPage: Int,
        val data: List<T> = emptyList(),
    ) : BasicState<T>

    class AllPagesLoaded<T>(
        val currentPage: Int,
        val data: List<T> = emptyList(),
    ) : BasicState<T>

    companion object {

        fun <T> create(
            state: Loading<T>,
            collectionList: BasicList<T>
        ): BasicState<T> {
            val canLoadMore = collectionList.results.isNotEmpty() &&
                state.data.size + collectionList.results.size < collectionList.count
            return if (canLoadMore) {
                CanLoadMore(
                    state.currentPage,
                    state.data + collectionList.results
                )
            } else {
                AllPagesLoaded(
                    state.currentPage,
                    state.data + collectionList.results
                )
            }
        }
    }
}
