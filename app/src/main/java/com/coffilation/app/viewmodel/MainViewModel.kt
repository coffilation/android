package com.coffilation.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coffilation.app.domain.BasicState
import com.coffilation.app.domain.BasicStateInteractor
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionPermissions
import com.coffilation.app.models.CollectionPermissionsRequestData
import com.coffilation.app.models.CollectionPointData
import com.coffilation.app.models.CollectionPointModifyRequestData
import com.coffilation.app.models.CollectionPointRequestData
import com.coffilation.app.models.PointData
import com.coffilation.app.models.SearchData
import com.coffilation.app.models.UserData
import com.coffilation.app.network.CollectionPermissionsRepository
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.network.MapRepository
import com.coffilation.app.network.SearchRepository
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.PAGE_SIZE
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.util.domain.ActionState
import com.coffilation.app.util.domain.actionModel
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
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response


class MainViewModel(
    publicCollectionsInteractor: BasicStateInteractor<CollectionData, Long>,
    userCollectionsInteractor: BasicStateInteractor<CollectionData, Long>,
    pointCollectionsInteractor: BasicStateInteractor<CollectionPointData, CollectionPointRequestData>,
    private val collectionsRepository: CollectionsRepository,
    private val searchRepository: SearchRepository,
    private val mapRepository: MapRepository,
    private val collectionPermissionsRepository: CollectionPermissionsRepository,
    private val usersRepository: UsersRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    private val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val suggestSession = searchManager.createSuggestSession()
    private val navigationBackStack = arrayListOf<MainViewState.MainViewStateMode>()

    private val publicCollectionsModel = publicCollectionsInteractor.getModel(viewModelScope, PAGE_SIZE)
    private val userCollectionsModel = userCollectionsInteractor.getModel(viewModelScope, PAGE_SIZE)

    private val searchResultsModel = actionModel<SearchData, List<PointData>>(viewModelScope) { requestData ->
        searchRepository.search(requestData)
    }

    private val pointCollectionsModel = pointCollectionsInteractor.getModel(viewModelScope, PAGE_SIZE)
    private val addPointToCollectionModel = actionModel<CollectionPointModifyRequestData, Response<Unit>>(viewModelScope) { requestData ->
        collectionsRepository.addPlaceToCollection(requestData.collectionId, requestData.pointId)
    }
    private val removePointFromCollectionModel = actionModel<CollectionPointModifyRequestData, Response<Unit>>(viewModelScope) { requestData ->
        collectionsRepository.removePlaceFromCollection(requestData.collectionId, requestData.pointId)
    }

    private val collectionPermissionsModel = actionModel<CollectionPermissionsRequestData, Array<CollectionPermissions>>(viewModelScope) { requestData ->
        collectionPermissionsRepository.getCollectionPermissions(requestData.collectionId, requestData.userId)
    }
    private val pointsForCollectionModel = actionModel<Long, List<PointData>>(viewModelScope) { collectionId ->
        mapRepository.getPointsForCollections(arrayOf(collectionId))
    }
    private val removeCollectionModel = actionModel<Long, Response<Unit>>(viewModelScope) { id ->
        collectionsRepository.removeCollection(id)
    }

    private val modeFlow = MutableStateFlow<MainViewState.MainViewStateMode>(MainViewState.MainViewStateMode.Collections)
    private val userDataFlow = MutableStateFlow<UseCaseResult<UserData>?>(null)

    private val searchQueryFlow = MutableStateFlow("")
    private val lastAppliedQueryFlow = MutableStateFlow<String?>(null)
    private val searchSuggestionsFlow = MutableStateFlow<UseCaseResult<List<SuggestItem>>?>(null)

    private val viewStateFlow = combine(
        modeFlow,
        userDataFlow,
        publicCollectionsModel.state,
        userCollectionsModel.state,
        lastAppliedQueryFlow,
        searchSuggestionsFlow,
        searchResultsModel.state,
        pointCollectionsModel.state,
        pointsForCollectionModel.state,
        collectionPermissionsModel.state,
    ) { data ->
        @Suppress("UNCHECKED_CAST")
        MainViewState.valueOf(
            data[0] as MainViewState.MainViewStateMode,
            data[1] as UseCaseResult<UserData>?,
            data[2] as BasicState<CollectionData>,
            data[3] as BasicState<CollectionData>,
            data[4] as String?,
            data[5] as UseCaseResult<List<SuggestItem>>?,
            data[6] as ActionState<SearchData, List<PointData>>,
            data[7] as BasicState<CollectionPointData>,
            data[8] as ActionState<Long, List<PointData>>,
            data[9] as ActionState<CollectionPermissionsRequestData, Array<CollectionPermissions>>,
        )
    }

    private val mutableAction = MutableLiveData<Action>()
    val viewState = viewStateFlow.asLiveData()
    val action: LiveData<Action> = mutableAction

    init {
        requestUserData()

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

        lastAppliedQueryFlow.filterNotNull().combine(
            modeFlow.filterIsInstance<MainViewState.MainViewStateMode.SearchResults>()
        ) { query, mode ->
            query to mode.boundingBox
        }.onEach { (query, boundingBox) ->
            searchResultsModel.action.invoke(SearchData.newInstance(boundingBox, query))
        }.launchIn(viewModelScope)

        lastAppliedQueryFlow.filterNotNull().onEach {
            searchQueryFlow.value = it
        }.launchIn(viewModelScope)

        userDataFlow.filterIsInstance<UseCaseResult.Error>().onEach {
            val isRequestUnauthorized = it.exception is HttpException && it.exception.code() == UNAUTHORIZED_CODE
            if (isRequestUnauthorized || !usersRepository.isAuthenticationSaved()) {
                mutableAction.value = Action.ShowSignIn
            }
        }.launchIn(viewModelScope)

        userDataFlow.filterIsInstance<UseCaseResult.Success<UserData>>().onEach { userData ->
            publicCollectionsModel.refresh.invoke(userData.data.id)
            userCollectionsModel.refresh.invoke(userData.data.id)
        }.launchIn(viewModelScope)

        merge(addPointToCollectionModel.state, removePointFromCollectionModel.state)
            .onEach { result ->
                if (result is ActionState.Success) {
                    val pointId = result.params.pointId
                    val userData = userDataFlow.value
                    if (userData is UseCaseResult.Success<UserData>) {
                        pointCollectionsModel.refresh.invoke(CollectionPointRequestData(userData.data.id, pointId))
                        pointsForCollectionModel.refresh.invoke()
                    }
                } else if (result is ActionState.Error) {
                    mutableAction.value = Action.ShowPointModifyError
                }
            }
            .launchIn(viewModelScope)

        removeCollectionModel.state
            .onEach { result ->
                if (result is ActionState.Success) {
                    updateUserCollections()
                    updatePublicCollections()
                    changeModeToCollections()
                } else if (result is ActionState.Error){
                    mutableAction.value = Action.ShowRemoveCollectionError
                }
            }.launchIn(viewModelScope)

        modeFlow.onEach { mode ->
            val duplicatingMode = navigationBackStack.filterIsInstance(mode::class.java).firstOrNull()
            if (duplicatingMode != null) {
                val duplicatingModeIndex = navigationBackStack.indexOf(duplicatingMode)
                navigationBackStack.subList(duplicatingModeIndex, navigationBackStack.size).clear()
            }
            navigationBackStack.add(mode)
        }.launchIn(viewModelScope)
    }

    fun updateUserCollections() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            userCollectionsModel.refresh.invoke(userData.data.id)
        }
    }

    private fun updatePublicCollections() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            publicCollectionsModel.refresh.invoke(userData.data.id)
        }
    }

    fun changeModeToCollections() {
        modeFlow.value = MainViewState.MainViewStateMode.Collections
    }

    fun changeModeToSearch(boundingBox: BoundingBox) {
        modeFlow.value = MainViewState.MainViewStateMode.Search(boundingBox)
    }

    fun changeModeToSearchResults(boundingBox: BoundingBox, scrollToPoint: PointData?) {
        modeFlow.value = MainViewState.MainViewStateMode.SearchResults(boundingBox, scrollToPoint)
    }

    fun changeModeToPointView(pointData: PointData) {
        modeFlow.value = MainViewState.MainViewStateMode.Point(pointData)
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            pointCollectionsModel.refresh.invoke(CollectionPointRequestData(userData.data.id, pointData.id))
        }
    }

    fun changeModeToCollectionView(collectionData: CollectionData, scrollToPoint: PointData?) {
        modeFlow.value = MainViewState.MainViewStateMode.Collection(collectionData, scrollToPoint)
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            pointsForCollectionModel.action.invoke(collectionData.id)
            collectionPermissionsModel.action.invoke(CollectionPermissionsRequestData(collectionData.id, userData.data.id))
        }
    }

    fun goToPreviousMode(): Boolean {
        return navigationBackStack.getOrNull(navigationBackStack.lastIndex - 1)?.let { mode ->
            modeFlow.value = mode
            true
        } ?: false
    }

    private fun requestUserData() {
        viewModelScope.launch {
            userDataFlow.value = usersRepository.me()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            prefRepository.putRefreshToken("")
            prefRepository.putAccessToken("")
            requestUserData()
        }
    }

    fun removeCollection(collectionId: Long) {
        removeCollectionModel.action.invoke(collectionId)
    }

    fun setSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun applySearchSuggestion(suggestItem: SuggestItem, boundingBox: BoundingBox) {
        lastAppliedQueryFlow.value = suggestItem.displayText
        changeModeToSearchResults(boundingBox, null)
    }

    fun showSearchResults(boundingBox: BoundingBox) {
        lastAppliedQueryFlow.value = searchQueryFlow.value
        changeModeToSearchResults(boundingBox, null)
    }

    fun selectPointInList(point: PointData) {
        val mode = modeFlow.value
        if (mode is MainViewState.MainViewStateMode.SearchResults) {
            changeModeToSearchResults(mode.boundingBox, point)
        } else if (mode is MainViewState.MainViewStateMode.Collection) {
            changeModeToCollectionView(mode.collectionData, point)
        }
    }

    fun onCollectionPointModified(collectionPointData: CollectionPointData) {
        val userData = userDataFlow.value
        val mode = modeFlow.value
        if (userData is UseCaseResult.Success<UserData> && mode is MainViewState.MainViewStateMode.Point) {
            val requestData = CollectionPointModifyRequestData(collectionPointData.id, mode.pointData.id)
            if (collectionPointData.isPlaceIncluded) {
                addPointToCollectionModel.action.invoke(requestData)
            } else {
                removePointFromCollectionModel.action.invoke(requestData)
            }
        }
    }

    fun onRetryPressed(id: Int) {
        when (id) {
            MainViewState.TYPE_USER -> {
                requestUserData()
            }
            MainViewState.TYPE_PUBLIC_COLLECTIONS -> {
                onPublicCollectionsListRetryPressed()
            }
            MainViewState.TYPE_USER_COLLECTIONS -> {
                onUserCollectionsListRetryPressed()
            }
            MainViewState.TYPE_SEARCH_RESULTS -> {
                searchResultsModel.retry.invoke()
            }
            MainViewState.TYPE_POINT_COLLECTIONS -> {
                onPointCollectionsListRetryPressed()
            }
            MainViewState.TYPE_COLLECTION -> {
                pointsForCollectionModel.retry.invoke()
            }
            MainViewState.TYPE_PERMISSIONS -> {
                collectionPermissionsModel.retry.invoke()
            }
        }
    }

    fun onPublicCollectionsListEndReached() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            publicCollectionsModel.nextPage.invoke(userData.data.id)
        }
    }

    fun onPublicCollectionsListRetryPressed() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            publicCollectionsModel.retry.invoke(userData.data.id)
        }
    }

    fun onUserCollectionsListEndReached() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            userCollectionsModel.nextPage.invoke(userData.data.id)
        }
    }

    private fun onUserCollectionsListRetryPressed() {
        val userData = userDataFlow.value
        if (userData is UseCaseResult.Success<UserData>) {
            userCollectionsModel.retry.invoke(userData.data.id)
        }
    }

    fun onPointCollectionsListEndReached() {
        val userData = userDataFlow.value
        val mode = modeFlow.value
        if (userData is UseCaseResult.Success<UserData> && mode is MainViewState.MainViewStateMode.Point) {
            val requestData = CollectionPointRequestData(userData.data.id, mode.pointData.id)
            pointCollectionsModel.nextPage.invoke(requestData)
        }
    }

    private fun onPointCollectionsListRetryPressed() {
        val userData = userDataFlow.value
        val mode = modeFlow.value
        if (userData is UseCaseResult.Success<UserData> && mode is MainViewState.MainViewStateMode.Point) {
            val requestData = CollectionPointRequestData(userData.data.id, mode.pointData.id)
            pointCollectionsModel.retry.invoke(requestData)
        }
    }

    fun onListEndReached() {
        when (modeFlow.value) {
            MainViewState.MainViewStateMode.Collections -> {
                onUserCollectionsListEndReached()
            }
            is MainViewState.MainViewStateMode.Point -> {
                onPointCollectionsListEndReached()
            }
            else -> {}
        }
    }

    sealed class Action {

        object ShowSignIn : Action()

        object ShowPointModifyError : Action()

        object ShowRemoveCollectionError : Action()
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
