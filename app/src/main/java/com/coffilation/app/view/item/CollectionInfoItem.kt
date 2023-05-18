package com.coffilation.app.view.item

import com.coffilation.app.models.CollectionData

/**
 * @author pvl-zolotov on 16.05.2023
 */
class CollectionInfoItem(val collection: CollectionData, val allowEdit: Boolean, val allowDelete: Boolean) : CardAdapterItem<Long>(collection.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as CollectionInfoItem
        return other.collection == collection && other.allowEdit == allowEdit && other.allowDelete == allowDelete
    }
}
