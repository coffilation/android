package com.coffilation.app.view.item

import com.coffilation.app.models.CollectionPointData

/**
 * @author pvl-zolotov on 14.05.2023
 */
class PointCollectionItem(val collection: CollectionPointData) : CardAdapterItem<Long>(collection.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as PointCollectionItem
        return other.collection == collection
    }
}
