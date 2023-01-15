package com.coffilation.app.view.item

/**
 * @author pvl-zolotov on 28.11.2022
 */
class SearchButtonWithNavigationItem(val query: String?) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchButtonWithNavigationItem).query == query
    }
}
