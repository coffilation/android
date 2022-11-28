package com.coffilation.app.view.viewstate

import com.coffilation.app.data.CollectionData
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.item.CardAdapterItem
import com.coffilation.app.view.item.ErrorItem
import com.coffilation.app.view.item.LoadingItem
import com.coffilation.app.view.item.PublicCollectionsListItem
import com.coffilation.app.view.item.SearchButtonItem
import com.coffilation.app.view.item.UserCollectionItem
import com.coffilation.app.view.item.UserCollectionsHeaderItem

/**
 * @author pvl-zolotov on 25.11.2022
 */
class MainViewState(val items: List<CardAdapterItem<*>>) {

    companion object {

        fun valueOf(
            username: String,
            userId: Long,
            publicCollections: UseCaseResult<List<CollectionData>>?,
            privateCollections: UseCaseResult<List<CollectionData>>?,
        ): MainViewState {
            val adapterItems = mutableListOf<CardAdapterItem<*>>()
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
            return MainViewState(adapterItems)
        }
    }
}
