package com.coffilation.app.view.item

import com.coffilation.app.models.PointData

/**
 * @author pvl-zolotov on 14.05.2023
 */
class PointInfoItem(val pointData: PointData) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as PointInfoItem
        return other.pointData == pointData
    }
}
