package com.coffilation.app.view.item

import com.coffilation.app.models.CollectionData

/**
 * @author pvl-zolotov on 25.11.2022
 */
class UserCollectionItem(val collection: CollectionData) : CardAdapterItem<Long>(collection.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as UserCollectionItem).collection == collection
    }
}
