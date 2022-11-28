package com.coffilation.app.view.item

import com.yandex.mapkit.search.SuggestItem

/**
 * @author pvl-zolotov on 27.11.2022
 */
class SearchSuggestionItem(val suggestion: SuggestItem) : CardAdapterItem<String>(suggestion.logId ?: "") {

    override fun areContentsTheSame(other: AdapterItem): Boolean {
        return (other as SearchSuggestionItem).suggestion == suggestion
    }
}
