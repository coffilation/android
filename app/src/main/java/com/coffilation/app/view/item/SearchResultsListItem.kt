package com.coffilation.app.view.item

import com.coffilation.app.models.PointData

/**
 * @author pvl-zolotov on 29.11.2022
 */
class SearchResultsListItem(val points: List<PointData>, val scrollToPoint: PointData?) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as SearchResultsListItem
        return other.points == points && other.scrollToPoint == scrollToPoint
    }
}
