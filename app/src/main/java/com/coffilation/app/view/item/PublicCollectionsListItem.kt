package com.coffilation.app.view.item

import com.coffilation.app.data.CollectionData

/**
 * @author pvl-zolotov on 25.11.2022
 */
class PublicCollectionsListItem(val collections: List<CollectionData>) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as PublicCollectionsListItem).collections == collections
    }
}
