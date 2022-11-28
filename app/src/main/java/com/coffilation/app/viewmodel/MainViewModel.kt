package com.coffilation.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coffilation.app.data.CollectionData
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.viewstate.MainViewState
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SuggestItem
import com.yandex.mapkit.search.SuggestOptions
import com.yandex.mapkit.search.SuggestSession
import com.yandex.mapkit.search.SuggestType
import com.yandex.runtime.Error
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainViewModel(
    private val collectionsRepository: CollectionsRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    private val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val suggestSession = searchManager.createSuggestSession()

    private val modeFlow = MutableStateFlow<MainViewState.MainViewStateMode>(MainViewState.MainViewStateMode.Collections)
    private val usernameFlow = MutableStateFlow<String?>(null)
    private val userIdFlow = MutableStateFlow<Long?>(null)
    private val publicCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)
    private val userCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)
    private val searchQueryFlow = MutableStateFlow("")
    private val lastAppliedSuggestionFlow = MutableStateFlow<String?>(null)
    private val searchSuggestionsFlow = MutableStateFlow<UseCaseResult<List<SuggestItem>>?>(null)

    private val viewStateFlow = combine(
        modeFlow,
        usernameFlow.filterNotNull(),
        userIdFlow.filterNotNull(),
        publicCollectionsFlow,
        userCollectionsFlow,
        lastAppliedSuggestionFlow,
        searchSuggestionsFlow,
    ) { data ->
        @Suppress("UNCHECKED_CAST")
        MainViewState.valueOf(
            data[0] as MainViewState.MainViewStateMode,
            data[1] as String,
            data[2] as Long,
            data[3] as UseCaseResult<List<CollectionData>>?,
            data[4] as UseCaseResult<List<CollectionData>>?,
            data[5] as String?,
            data[6] as UseCaseResult<List<SuggestItem>>?,
        )
    }

    val viewState = viewStateFlow.asLiveData()

    init {
        viewModelScope.launch {
            usernameFlow.value = prefRepository.getUsername()
            val userId = prefRepository.getUserId()
            userIdFlow.value = userId
            publicCollectionsFlow.value = collectionsRepository.getPublicCollections()
            userCollectionsFlow.value = collectionsRepository.getUserCollections(userId)

            @OptIn(ExperimentalCoroutinesApi::class)
            searchQueryFlow.combine(
                modeFlow.filterIsInstance<MainViewState.MainViewStateMode.Search>()
            ) { query, mode ->
                query to mode.boundingBox
            }.flatMapLatest { (query, boundingBox) ->
                callbackFlow<UseCaseResult<List<SuggestItem>>?> {
                    val suggestListener = object : SuggestSession.SuggestListener {

                        override fun onResponse(data: MutableList<SuggestItem>) {
                            trySend(UseCaseResult.Success(data))
                        }

                        @Suppress("ThrowableNotThrown")
                        override fun onError(error: Error) {
                            trySend(UseCaseResult.Error(Exception("Something went wrong")))
                        }
                    }

                    suggestSession.suggest(query, boundingBox, SEARCH_OPTIONS, suggestListener)
                    awaitClose { suggestSession.reset() }
                }
            }.onEach {
                searchSuggestionsFlow.value = it
            }.collect()

            lastAppliedSuggestionFlow.filterNotNull().onEach {
                searchQueryFlow.value = it
            }.collect()
        }
    }

    fun updateUserCollections() {
        viewModelScope.launch {
            val userId = userIdFlow.value
            if (userId != null) {
                userCollectionsFlow.value = collectionsRepository.getUserCollections(userId)
            }
        }
    }

    fun changeModeToCollections() {
        modeFlow.value = MainViewState.MainViewStateMode.Collections
    }

    fun changeModeToSearch(boundingBox: BoundingBox) {
        modeFlow.value = MainViewState.MainViewStateMode.Search(boundingBox)
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun applySearchSuggestion(suggestItem: SuggestItem) {
        lastAppliedSuggestionFlow.value = suggestItem.displayText
    }

    companion object {

        private val SEARCH_OPTIONS = SuggestOptions().setSuggestTypes(
            SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value
        )
    }
}
