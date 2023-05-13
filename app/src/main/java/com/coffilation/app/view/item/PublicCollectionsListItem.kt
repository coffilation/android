package com.coffilation.app.view.item

import com.coffilation.app.util.elementEquals

/**
 * @author pvl-zolotov on 25.11.2022
 */
class PublicCollectionsListItem(val items: List<CardAdapterItem<*>>, val autoLoadingEnabled: Boolean) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as PublicCollectionsListItem
        return other.autoLoadingEnabled == autoLoadingEnabled &&
            other.items.elementEquals(items) { (first, second) ->
                first.areItemsTheSame(second) && first.areContentsTheSame(second)
            }
    }
}
