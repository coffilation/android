package com.coffilation.app.view.viewstate

import com.coffilation.app.data.CollectionData
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.item.CardAdapterItem
import com.coffilation.app.view.item.ErrorItem
import com.coffilation.app.view.item.LoadingItem
import com.coffilation.app.view.item.PublicCollectionsListItem
import com.coffilation.app.view.item.SearchButtonItem
import com.coffilation.app.view.item.SearchInputItem
import com.coffilation.app.view.item.SearchSuggestionItem
import com.coffilation.app.view.item.UserCollectionItem
import com.coffilation.app.view.item.UserCollectionsHeaderItem
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.search.SuggestItem

/**
 * @author pvl-zolotov on 25.11.2022
 */
class MainViewState(val items: List<CardAdapterItem<*>>, val minBottomSheetHeightAllowed: BottomSheetState) {

    companion object {

        fun valueOf(
            mode: MainViewStateMode,
            username: String,
            userId: Long,
            publicCollections: UseCaseResult<List<CollectionData>>?,
            privateCollections: UseCaseResult<List<CollectionData>>?,
            lastAppliedSuggestion: String?,
            searchSuggestions: UseCaseResult<List<SuggestItem>>?,
        ): MainViewState {
            val adapterItems = mutableListOf<CardAdapterItem<*>>()
            if (mode == MainViewStateMode.Collections) {
                adapterItems.add(SearchButtonItem(username))
                if (
                    publicCollections is UseCaseResult.Success &&
                    privateCollections is UseCaseResult.Success
                ) {
                    adapterItems.add(PublicCollectionsListItem(publicCollections.data.filter { it.author.id != userId }))
                    adapterItems.add(UserCollectionsHeaderItem())
                    privateCollections.data.forEach {
                        adapterItems.add(UserCollectionItem(it))
                    }
                } else if (
                    publicCollections is UseCaseResult.Error ||
                    privateCollections is UseCaseResult.Error
                ) {
                    adapterItems.add(ErrorItem())
                } else {
                    adapterItems.add(LoadingItem())
                }
                return MainViewState(adapterItems, BottomSheetState.PEEKED)
            } else {
                adapterItems.add(SearchInputItem(lastAppliedSuggestion))
                if (searchSuggestions is UseCaseResult.Success) {
                    searchSuggestions.data.forEach {
                        adapterItems.add(SearchSuggestionItem(it))
                    }
                } else if (searchSuggestions is UseCaseResult.Error) {
                    adapterItems.add(ErrorItem())
                } else {
                    adapterItems.add(LoadingItem())
                }
                return MainViewState(adapterItems, BottomSheetState.FULLSCREEN)
            }
        }
    }

    sealed class MainViewStateMode {

        object Collections : MainViewStateMode()
        class Search(val boundingBox: BoundingBox) : MainViewStateMode()
    }

    enum class BottomSheetState {

        FULLSCREEN,
        PEEKED
    }
}
