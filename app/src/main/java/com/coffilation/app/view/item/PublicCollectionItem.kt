package com.coffilation.app.view.item

import com.coffilation.app.models.CollectionData

/**
 * @author pvl-zolotov on 27.11.2022
 */
class PublicCollectionItem(val collection: CollectionData) : CardAdapterItem<Long>(collection.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as PublicCollectionItem).collection == collection
    }
}
