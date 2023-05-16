package com.coffilation.app.view.item

import com.coffilation.app.models.CollectionData

/**
 * @author pvl-zolotov on 16.05.2023
 */
class CollectionInfoItem(val collection: CollectionData) : CardAdapterItem<Long>(collection.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as CollectionInfoItem).collection == collection
    }
}
