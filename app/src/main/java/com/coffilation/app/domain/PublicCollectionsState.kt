package com.coffilation.app.domain

import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionList

/**
 * @author pvl-zolotov on 09.05.2023
 */
sealed interface PublicCollectionsState {

    object Initial : PublicCollectionsState

    class Loading(
        val currentPage: Int = 0,
        val collections: List<CollectionData> = emptyList(),
    ) : PublicCollectionsState

    class LoadingError(
        val currentPage: Int,
        val error: Throwable,
        val collections: List<CollectionData> = emptyList(),
    ) : PublicCollectionsState

    class CanLoadMore(
        val currentPage: Int,
        val collections: List<CollectionData> = emptyList(),
    ) : PublicCollectionsState

    class AllPagesLoaded(
        val currentPage: Int,
        val collections: List<CollectionData> = emptyList(),
    ) : PublicCollectionsState

    companion object {

        fun create(
            state: Loading,
            collectionList: CollectionList
        ): PublicCollectionsState {
            val canLoadMore = collectionList.collections.isNotEmpty() &&
                state.collections.size + collectionList.collections.size < collectionList.count
            return if (canLoadMore) {
                CanLoadMore(
                    state.currentPage,
                    state.collections + collectionList.collections
                )
            } else {
                AllPagesLoaded(
                    state.currentPage,
                    state.collections + collectionList.collections
                )
            }
        }
    }
}
