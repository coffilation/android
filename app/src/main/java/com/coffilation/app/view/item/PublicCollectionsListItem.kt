package com.coffilation.app.view.item

/**
 * @author pvl-zolotov on 25.11.2022
 */
class PublicCollectionsListItem(val items: List<CardAdapterItem<*>>, val autoLoadingEnabled: Boolean) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        other as PublicCollectionsListItem
        return other.items == items && other.autoLoadingEnabled == autoLoadingEnabled
    }
}
