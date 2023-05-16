package com.coffilation.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.CollectionData
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 21.11.2022
 */
class EditCollectionViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    private val mutableAction = MutableLiveData<Action>()
    val action: LiveData<Action> = mutableAction

    suspend fun addCollection(collectionAddData: CollectionAddData) {
        val result = collectionsRepository.addCollection(collectionAddData)
        if (result is UseCaseResult.Success<CollectionData>) {
            mutableAction.value = Action.CollectionCreationFinished(result.data)
        } else {
            mutableAction.value = Action.ShowSavingError
        }
    }

    suspend fun editCollection(collectionId: Long, collectionAddData: CollectionAddData) {
        val result = collectionsRepository.editCollection(collectionId, collectionAddData)
        if (result is UseCaseResult.Success<CollectionData>) {
            mutableAction.value = Action.CollectionEditingFinished(result.data)
        } else {
            mutableAction.value = Action.ShowSavingError
        }
    }

    sealed class Action {

        class CollectionCreationFinished(val collection: CollectionData) : Action()

        class CollectionEditingFinished(val collection: CollectionData) : Action()

        object ShowSavingError : Action()
    }
}
