package com.coffilation.app.view.item

/**
 * @author pvl-zolotov on 28.11.2022
 */
class SearchInputItem(val lastAppliedSuggestion: String?) : CardAdapterItem<Int>(0) {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchInputItem).lastAppliedSuggestion == lastAppliedSuggestion
    }
}
