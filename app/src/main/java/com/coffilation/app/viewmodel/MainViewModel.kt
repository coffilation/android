package com.coffilation.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.coffilation.app.data.CollectionData
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.view.viewstate.MainViewState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.startWith
import kotlinx.coroutines.launch

class MainViewModel(
    private val collectionsRepository: CollectionsRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    private val usernameFlow = MutableStateFlow<String?>(null)
    private val userIdFlow = MutableStateFlow<Long?>(null)
    private val publicCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)
    private val userCollectionsFlow = MutableStateFlow<UseCaseResult<List<CollectionData>>?>(null)

    private val viewStateFlow = combine(
        usernameFlow.filterNotNull(),
        userIdFlow.filterNotNull(),
        publicCollectionsFlow,
        userCollectionsFlow,
        MainViewState::valueOf
    )

    val viewState = viewStateFlow.asLiveData()

    init {
        viewModelScope.launch {
            usernameFlow.value = prefRepository.getUsername()
            val userId = prefRepository.getUserId()
            userIdFlow.value = userId
            publicCollectionsFlow.value = collectionsRepository.getPublicCollections()
            userCollectionsFlow.value = collectionsRepository.getUserCollections(userId)
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
}
