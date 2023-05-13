package com.coffilation.app.view.viewstate

import com.coffilation.app.domain.BasicState
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.PointData
import com.coffilation.app.models.UserData
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.item.CardAdapterItem
import com.coffilation.app.view.item.DragHandleItem
import com.coffilation.app.view.item.EmptyItem
import com.coffilation.app.view.item.ErrorItem
import com.coffilation.app.view.item.LoadingItem
import com.coffilation.app.view.item.PublicCollectionItem
import com.coffilation.app.view.item.PublicCollectionsListItem
import com.coffilation.app.view.item.SearchButtonItem
import com.coffilation.app.view.item.SearchButtonWithNavigationItem
import com.coffilation.app.view.item.SearchInputItem
import com.coffilation.app.view.item.SearchResultsListItem
import com.coffilation.app.view.item.SearchSuggestionItem
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
) {

    companion object {

        const val TYPE_USER = 0
        const val TYPE_PUBLIC_COLLECTIONS = 1

        fun valueOf(
            mode: MainViewStateMode,
            userData: UseCaseResult<UserData>?,
            publicCollections: BasicState<CollectionData>,
            privateCollections: UseCaseResult<List<CollectionData>>?,
            lastAppliedSuggestion: String?,
            searchSuggestions: UseCaseResult<List<SuggestItem>>?,
            searchResults: UseCaseResult<List<PointData>>?,
            detailedPoint: UseCaseResult<PointData>?,
        ): MainViewState {
            val adapterItems = mutableListOf<CardAdapterItem<*>>()
            adapterItems.add(DragHandleItem())
            when (mode) {
                MainViewStateMode.Collections -> {
                    if (userData is UseCaseResult.Error) {
                        adapterItems.add(SearchButtonItem(null))
                        adapterItems.add(ErrorItem(TYPE_USER))
                    } else if (userData is UseCaseResult.Success) {
                        adapterItems.add(SearchButtonItem(userData.data.username))
                        when (publicCollections) {
                            is BasicState.Initial -> {
                                adapterItems.add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                            }
                            is BasicState.Loading -> {
                                val data = publicCollections.data
                                if (data.isNotEmpty()) {
                                    val items = arrayListOf<CardAdapterItem<*>>()
                                    items.addAll(data.map { PublicCollectionItem(it) })
                                    items.add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                                    adapterItems.add(PublicCollectionsListItem(items, false))
                                } else {
                                    adapterItems.add(LoadingItem(TYPE_PUBLIC_COLLECTIONS))
                                }
                            }
                            is BasicState.AllPagesLoaded -> {
                                val data = publicCollections.data
                                adapterItems.add(PublicCollectionsListItem(data.map { PublicCollectionItem(it) }, false))
                            }
                            is BasicState.CanLoadMore -> {
                                val data = publicCollections.data
                                adapterItems.add(PublicCollectionsListItem(data.map { PublicCollectionItem(it) }, true))
                            }
                            is BasicState.LoadingError -> {
                                val data = publicCollections.data
                                if (data.isNotEmpty()) {
                                    val items = arrayListOf<CardAdapterItem<*>>()
                                    items.addAll(data.map { PublicCollectionItem(it) })
                                    items.add(ErrorItem(TYPE_PUBLIC_COLLECTIONS))
                                    adapterItems.add(PublicCollectionsListItem(items, false))
                                } else {
                                    adapterItems.add(ErrorItem(TYPE_PUBLIC_COLLECTIONS))
                                }
                            }
                        }
                    }
                    /*if (
                        publicCollections is UseCaseResult.Success &&
                        privateCollections is UseCaseResult.Success
                    ) {
                        adapterItems.add(
                            PublicCollectionsListItem(publicCollections.data.filter { it.author.id != user?.data?.id })
                        )
                        adapterItems.add(UserCollectionsHeaderItem())
                        if (privateCollections.data.isNotEmpty()) {
                            privateCollections.data.forEach {
                                adapterItems.add(UserCollectionItem(it))
                            }
                        } else {
                            adapterItems.add(EmptyItem())
                        }
                    } else if (
                        publicCollections is UseCaseResult.Error ||
                        privateCollections is UseCaseResult.Error
                    ) {
                        adapterItems.add(ErrorItem())
                    } else {
                        adapterItems.add(LoadingItem())
                    }*/
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.PEEKED_COMPACT, BottomSheetState.FULLSCREEN),
                        false,
                        null
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
                        adapterItems.add(ErrorItem())
                    } else {
                        adapterItems.add(LoadingItem())
                    }
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.FULLSCREEN, BottomSheetState.FULLSCREEN),
                        true,
                        null
                    )
                }
                is MainViewStateMode.SearchResults -> {
                    adapterItems.add(SearchButtonWithNavigationItem(lastAppliedSuggestion))
                    var points = emptyList<PointData>()
                    if (searchResults is UseCaseResult.Success) {
                        if (searchResults.data.isNotEmpty()) {
                            adapterItems.add(SearchResultsListItem(searchResults.data))
                            points = searchResults.data
                        } else {
                            adapterItems.add(EmptyItem())
                        }
                    } else if (searchResults is UseCaseResult.Error) {
                        adapterItems.add(ErrorItem())
                    } else {
                        adapterItems.add(LoadingItem())
                    }
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.PEEKED_MEDIUM, BottomSheetState.PEEKED_MEDIUM),
                        false,
                        points
                    )
                }
                is MainViewStateMode.Point -> {
                    return MainViewState(
                        adapterItems,
                        BottomSheetConfig(BottomSheetState.PEEKED_MEDIUM, BottomSheetState.FULLSCREEN),
                        false,
                        null
                    )
                }
            }
        }
    }

    sealed class MainViewStateMode {

        object Collections : MainViewStateMode()
        class Search(val boundingBox: BoundingBox) : MainViewStateMode()
        class SearchResults(val boundingBox: BoundingBox) : MainViewStateMode()
        class Point(val osmId: Long, val osmType: String, val category: String) : MainViewStateMode()
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
