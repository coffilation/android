package com.coffilation.app.view.item

import com.coffilation.app.data.PointData

/**
 * @author pvl-zolotov on 29.11.2022
 */
class SearchResultItem(val point: PointData) : CardAdapterItem<Long>(point.id) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchResultItem).point == point
    }
}
