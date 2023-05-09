package com.coffilation.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 21.11.2022
 */
class EditCollectionViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    suspend fun addCollection(collectionAddData: CollectionAddData) {
        val result = collectionsRepository.addCollection(collectionAddData)
        when (result) {
            is UseCaseResult.Success -> {
                Log.v("test - collection add", result.data.id.toString())
            }
            is UseCaseResult.Error -> Log.v("test", "error")
        }
    }
}
