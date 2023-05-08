package com.coffilation.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coffilation.app.data.CollectionData
import com.coffilation.app.data.PointData
import com.coffilation.app.data.SearchData
import com.coffilation.app.data.UserData
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.network.SearchRepository
import com.coffilation.app.network.UsersRepository
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.HttpException


class MainViewModel(
    private val collectionsRepository: CollectionsRepository,
    private val searchRepository: SearchRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val suggestSession = searchManager.createSuggestSession()

    private val modeFlow = MutableStateFlow<MainViewState.MainViewStateMode>(MainViewState.MainViewStateMode.Collections)
    private val userDataFlow = MutableStateFlow<UseCaseResult<UserData>?>(null)
    private val publicCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)
    private val userCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)
    private val searchQueryFlow = MutableStateFlow("")
    private val lastAppliedSuggestionFlow = MutableStateFlow<String?>(null)
    private val searchSuggestionsFlow = MutableStateFlow<UseCaseResult<List<SuggestItem>>?>(null)
    private val searchResultsFlow = MutableStateFlow<UseCaseResult<List<PointData>>?>(null)
    private val detailedPointFlow = MutableStateFlow<PointData?>(null)

    private val viewStateFlow = combine(
        modeFlow,
        userDataFlow,
        publicCollectionsFlow,
        userCollectionsFlow,
        lastAppliedSuggestionFlow,
        searchSuggestionsFlow,
        searchResultsFlow,
        detailedPointFlow,
    ) { data ->
        @Suppress("UNCHECKED_CAST")
        MainViewState.valueOf(
            data[0] as MainViewState.MainViewStateMode,
            data[1] as UseCaseResult<UserData>?,
            data[2] as UseCaseResult<List<CollectionData>>?,
            data[3] as UseCaseResult<List<CollectionData>>?,
            data[4] as String?,
            data[5] as UseCaseResult<List<SuggestItem>>?,
            data[6] as UseCaseResult<List<PointData>>?,
            data[7] as UseCaseResult<PointData>?,
        )
    }

    private val mutableAction = MutableLiveData<Action>()
    val viewState = viewStateFlow.asLiveData()
    val action: LiveData<Action> = mutableAction

    init {
        viewModelScope.launch {
            userDataFlow.value = usersRepository.me()
        }

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
        }.launchIn(viewModelScope)

        searchQueryFlow.combine(
            modeFlow.filterIsInstance<MainViewState.MainViewStateMode.SearchResults>()
        ) { query, mode ->
            query to mode.boundingBox
        }.map { (query, boundingBox) ->
            searchRepository.search(SearchData.newInstance(boundingBox, query))
        }.onEach {
            searchResultsFlow.value = it
        }.launchIn(viewModelScope)

        lastAppliedSuggestionFlow.filterNotNull().onEach {
            searchQueryFlow.value = it
        }.launchIn(viewModelScope)

        userDataFlow.filterIsInstance<UseCaseResult.Error>().onEach {
            val isRequestUnauthorized = it.exception is HttpException && it.exception.code() == UNAUTHORIZED_CODE
            if (isRequestUnauthorized || !usersRepository.isAuthenticationSaved()) {
                mutableAction.value = Action.ShowSignIn
            }
        }.launchIn(viewModelScope)

        userDataFlow.filterIsInstance<UseCaseResult.Success<UserData>>().onEach { userData ->
            publicCollectionsFlow.value = collectionsRepository.getPublicCollections()
            userCollectionsFlow.value = collectionsRepository.getUserCollections(userData.data.id)
        }.launchIn(viewModelScope)
    }

    fun updateUserCollections() {
        viewModelScope.launch {
            val userData = userDataFlow.value
            if (userData is UseCaseResult.Success<UserData>) {
                userCollectionsFlow.value = collectionsRepository.getUserCollections(userData.data.id)
            }
        }
    }

    fun changeModeToCollections() {
        modeFlow.value = MainViewState.MainViewStateMode.Collections
    }

    fun changeModeToSearch(boundingBox: BoundingBox) {
        modeFlow.value = MainViewState.MainViewStateMode.Search(boundingBox)
    }

    fun changeModeToSearchResults(boundingBox: BoundingBox) {
        modeFlow.value = MainViewState.MainViewStateMode.SearchResults(boundingBox)
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun applySearchSuggestion(suggestItem: SuggestItem) {
        lastAppliedSuggestionFlow.value = suggestItem.displayText
    }

    fun showSearchResults(boundingBox: BoundingBox) {
        lastAppliedSuggestionFlow.value = searchQueryFlow.value
        changeModeToSearchResults(boundingBox)

        /*val objectArrayString: String = context.resources.openRawResource(R.raw.resp).bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<PointData>>() {}.type
        val objectArray = Gson().fromJson<List<PointData>>(objectArrayString, listType)
        searchResultsFlow.value = UseCaseResult.Success(objectArray as List<PointData>)*/
    }

    fun showDetailedPoint(pointData: PointData) {

    }

    /*fun selectPoint(point: PointData) {
        selectedPointFlow.value = point
    }*/

    sealed class Action {

        object ShowSignIn : Action()
    }

    companion object {

        private const val UNAUTHORIZED_CODE = 401

        private val SEARCH_OPTIONS = SuggestOptions().setSuggestTypes(
            SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value
        )
    }
}
