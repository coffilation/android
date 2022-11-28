package com.coffilation.app.network

import com.coffilation.app.data.CollectionAddData
import com.coffilation.app.data.CollectionAddResult
import com.coffilation.app.data.CollectionData
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 21.11.2022
 */
interface CollectionsRepository {

    suspend fun addCollection(collectionAddData: CollectionAddData): UseCaseResult<CollectionAddResult>

    suspend fun getUserCollections(userId: Long): UseCaseResult<List<CollectionData>>

    suspend fun getPublicCollections(): UseCaseResult<List<CollectionData>>
}

class CollectionRepositoryImpl(private val collectionsApi: CollectionsApi) : CollectionsRepository {

    override suspend fun addCollection(collectionAddData: CollectionAddData): UseCaseResult<CollectionAddResult> {
        return try {
            val result = collectionsApi.addCollection(collectionAddData)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun getUserCollections(userId: Long): UseCaseResult<List<CollectionData>> {
        return try {
            val result = collectionsApi.getUserCollections(userId)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun getPublicCollections(): UseCaseResult<List<CollectionData>> {
        return try {
            val result = collectionsApi.getPublicCollections()
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
