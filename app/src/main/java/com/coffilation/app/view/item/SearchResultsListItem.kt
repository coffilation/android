package com.coffilation.app.view.item

import com.coffilation.app.data.PointData

/**
 * @author pvl-zolotov on 29.11.2022
 */
class SearchResultsListItem(val points: List<PointData>) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchResultsListItem).points == points
    }
}
