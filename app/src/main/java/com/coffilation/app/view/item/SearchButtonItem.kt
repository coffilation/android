package com.coffilation.app.view.item

/**
 * @author pvl-zolotov on 25.11.2022
 */
class SearchButtonItem(val username: String) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchButtonItem).username == username
    }
}
