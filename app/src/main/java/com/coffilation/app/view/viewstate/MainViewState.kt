package com.coffilation.app.view.viewstate

import com.coffilation.app.domain.BasicState
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionPointData
import com.coffilation.app.models.PointData
import com.coffilation.app.models.UserData
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.item.CardAdapterItem
import com.coffilation.app.view.item.DragHandleItem
import com.coffilation.app.view.item.EmptyItem
import com.coffilation.app.view.item.ErrorItem
import com.coffilation.app.view.item.LoadingItem
import com.coffilation.app.view.item.PointCollectionItem
import com.coffilation.app.view.item.PointInfoItem
import com.coffilation.app.view.item.PublicCollectionItem
import com.coffilation.app.view.item.PublicCollectionsListItem
import com.coffilation.app.view.item.SearchButtonItem
import com.coffilation.app.view.item.SearchButtonWithNavigationItem
import com.coffilation.app.view.item.SearchInputItem
import com.coffilation.app.view.item.SearchResultsListItem
import com.coffilation.app.view.item.SearchSuggestionItem
import com.coffilation.app.view.item.UserCollectionItem
import com.coffilation.app.view.item.UserCollectionsHeaderItem
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.search.SuggestItem

/**
 * @author pvl-zolotov on 25.11.2022
 */
class MainViewState(
    val items: List<CardAdapterItem<*>>,
    val bottomSheetConfig: BottomSheetConfig,
    val allowShowKeyboard: Boolean,
    val points: List<PointData>?,
    val selectedPoint: PointData?,
    val autoLoadingEnabled: Boolean,
) {

    companion object {

        const val TYPE_USER = 0
        const val TYPE_PUBLIC_COLLECTIONS = 1
        const val TYPE_USER_COLLECTIONS = 2
        const val TYPE_SEARCH = 3
        const val TYPE_SEARCH_RESULTS = 4
        const val TYPE_POINT_COLLECTIONS = 5

        fun valueOf(
            mode: MainViewStateMode,
            userData: UseCaseResult<UserData>,
            publicCollections: BasicState<CollectionData>,
            userCollections: BasicState<CollectionData>,
            lastAppliedSuggestion: String?,
            searchSuggestions: UseCaseResult<List<SuggestItem>>?,
            searchResults: UseCaseResult<List<PointData>>?,
            pointCollections: BasicState<CollectionPointData>,
        ): MainViewState {
            val adapterItems = mutableListOf<CardAdapterItem<*>>()
            adapterItems.add(DragHandleItem())
            when (mode) {
                MainViewStateMode.Collections -> {
                    val autoLoadingEnabled: Boolean
                    when (userData) {
                        is UseCaseResult.Error -> {
                            adapterItems.add(SearchButtonItem(null))
                            adapterItems.add(ErrorItem(TYPE_USER))
                            autoLoadingEnabled = false
                        }
                        is UseCaseResult.Success -> {
                            adapterItems.add(SearchButtonItem(userData.data.username))
                            adapterItems.addPublicCollections(publicCollections)
                            autoLoadingEnabled = adapterItems.addUserCollections(userCollections)
                        }
                    }
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.PEEKED_COMPACT, BottomSheetState.FULLSCREEN),
                        false,
                        null,
                        null,
                        autoLoadingEnabled
                    )
                }
                is MainViewStateMode.Search -> {
                    adapterItems.add(SearchInputItem(lastAppliedSuggestion))
                    if (searchSuggestions is UseCaseResult.Success) {
                        if (searchSuggestions.data.isNotEmpty()) {
                            searchSuggestions.data.forEach {
                                adapterItems.add(SearchSuggestionItem(it))
                            }
                        } else {
                            adapterItems.add(EmptyItem())
                        }
                    } else if (searchSuggestions is UseCaseResult.Error) {
                        adapterItems.add(ErrorItem(TYPE_SEARCH))
                    } else {
                        adapterItems.add(LoadingItem())
                    }
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.FULLSCREEN, BottomSheetState.FULLSCREEN),
                        true,
                        null,
                        null,
                        false
                    )
                }
                is MainViewStateMode.SearchResults -> {
                    adapterItems.add(SearchButtonWithNavigationItem(lastAppliedSuggestion))
                    var points = emptyList<PointData>()
                    if (searchResults is UseCaseResult.Success) {
                        if (searchResults.data.isNotEmpty()) {
                            adapterItems.add(SearchResultsListItem(searchResults.data, mode.scrollToPoint))
                            points = searchResults.data
                        } else {
                            adapterItems.add(EmptyItem())
                        }
                    } else if (searchResults is UseCaseResult.Error) {
                        adapterItems.add(ErrorItem(TYPE_SEARCH_RESULTS))
                    } else {
                        adapterItems.add(LoadingItem())
                    }
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.PEEKED_MEDIUM, BottomSheetState.PEEKED_MEDIUM),
                        false,
                        points,
                        mode.scrollToPoint,
                        false
                    )
                }
                is MainViewStateMode.Point -> {
                    adapterItems.add(PointInfoItem(mode.pointData))
                    adapterItems.addPointCollections(pointCollections)
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.FULLSCREEN, BottomSheetState.FULLSCREEN),
                        false,
                        null,
                        null,
                        false
                    )
                }
            }
        }

        private fun MutableList<CardAdapterItem<*>>.addPublicCollections(publicCollections: BasicState<CollectionData>) {
            when (publicCollections) {
                is BasicState.Initial -> {
                    add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                }
                is BasicState.Loading -> {
                    val data = publicCollections.data
                    if (data.isNotEmpty()) {
                        val items = arrayListOf<CardAdapterItem<*>>()
                        items.addAll(data.map { PublicCollectionItem(it) })
                        items.add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                        add(PublicCollectionsListItem(items, false))
                    } else {
                        add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                    }
                }
                is BasicState.AllPagesLoaded -> {
                    val data = publicCollections.data
                    add(PublicCollectionsListItem(data.map { PublicCollectionItem(it) }, false))
                }
                is BasicState.CanLoadMore -> {
                    val data = publicCollections.data
                    add(PublicCollectionsListItem(data.map { PublicCollectionItem(it) }, true))
                }
                is BasicState.LoadingError -> {
                    val data = publicCollections.data
                    if (data.isNotEmpty()) {
                        val items = arrayListOf<CardAdapterItem<*>>()
                        items.addAll(data.map { PublicCollectionItem(it) })
                        items.add(ErrorItem(TYPE_PUBLIC_COLLECTIONS))
                        add(PublicCollectionsListItem(items, false))
                    } else {
                        add(ErrorItem(TYPE_PUBLIC_COLLECTIONS))
                    }
                }
            }
        }

        private fun MutableList<CardAdapterItem<*>>.addUserCollections(userCollections: BasicState<CollectionData>): Boolean {
            val autoLoadingEnabled: Boolean
            add(UserCollectionsHeaderItem())
            when (userCollections) {
                is BasicState.Initial -> {
                    add(LoadingItem(TYPE_USER_COLLECTIONS))
                    autoLoadingEnabled = false
                }
                is BasicState.Loading -> {
                    if (userCollections.data.isNotEmpty()) {
                        userCollections.data.forEach {
                            add(UserCollectionItem(it))
                        }
                    }
                    add(LoadingItem(TYPE_USER_COLLECTIONS))
                    autoLoadingEnabled = false
                }
                is BasicState.AllPagesLoaded -> {
                    if (userCollections.data.isNotEmpty()) {
                        userCollections.data.forEach {
                            add(UserCollectionItem(it))
                        }
                    } else {
                        add(EmptyItem())
                    }
                    autoLoadingEnabled = false
                }
                is BasicState.CanLoadMore -> {
                    if (userCollections.data.isNotEmpty()) {
                        userCollections.data.forEach {
                            add(UserCollectionItem(it))
                        }
                    }
                    autoLoadingEnabled = true
                }
                is BasicState.LoadingError -> {
                    if (userCollections.data.isNotEmpty()) {
                        userCollections.data.forEach {
                            add(UserCollectionItem(it))
                        }
                    }
                    add(ErrorItem(TYPE_USER_COLLECTIONS))
                    autoLoadingEnabled = false
                }
            }
            return autoLoadingEnabled
        }

        private fun MutableList<CardAdapterItem<*>>.addPointCollections(pointCollections: BasicState<CollectionPointData>): Boolean {
            val autoLoadingEnabled: Boolean
            when (pointCollections) {
                is BasicState.Initial -> {
                    add(LoadingItem(TYPE_POINT_COLLECTIONS))
                    autoLoadingEnabled = false
                }
                is BasicState.Loading -> {
                    if (pointCollections.data.isNotEmpty()) {
                        pointCollections.data.forEach {
                            add(PointCollectionItem(it))
                        }
                    }
                    add(LoadingItem(TYPE_POINT_COLLECTIONS))
                    autoLoadingEnabled = false
                }
                is BasicState.AllPagesLoaded -> {
                    if (pointCollections.data.isNotEmpty()) {
                        pointCollections.data.forEach {
                            add(PointCollectionItem(it))
                        }
                    } else {
                        add(EmptyItem())
                    }
                    autoLoadingEnabled = false
                }
                is BasicState.CanLoadMore -> {
                    if (pointCollections.data.isNotEmpty()) {
                        pointCollections.data.forEach {
                            add(PointCollectionItem(it))
                        }
                    }
                    autoLoadingEnabled = true
                }
                is BasicState.LoadingError -> {
                    if (pointCollections.data.isNotEmpty()) {
                        pointCollections.data.forEach {
                            add(PointCollectionItem(it))
                        }
                    }
                    add(ErrorItem(TYPE_POINT_COLLECTIONS))
                    autoLoadingEnabled = false
                }
            }
            return autoLoadingEnabled
        }
    }

    sealed class MainViewStateMode {

        object Collections : MainViewStateMode()
        class Search(val boundingBox: BoundingBox) : MainViewStateMode()
        class SearchResults(val boundingBox: BoundingBox, val scrollToPoint: PointData?) : MainViewStateMode()
        class Point(val pointData: PointData) : MainViewStateMode()
    }

    data class BottomSheetConfig(
        val minHeight: BottomSheetState,
        val maxHeight: BottomSheetState,
    )

    enum class BottomSheetState {

        FULLSCREEN,
        PEEKED_COMPACT,
        PEEKED_MEDIUM
    }
}
