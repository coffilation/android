package com.coffilation.app.network

import com.coffilation.app.models.BasicList
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.CollectionAddResult
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionPointData
import com.coffilation.app.models.PlacesToModifyData
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 21.11.2022
 */
interface CollectionsRepository {

    suspend fun addCollection(collectionAddData: CollectionAddData): UseCaseResult<CollectionAddResult>

    suspend fun getUserCollections(page: Int, pageSize: Int, userId: Long): UseCaseResult<BasicList<CollectionData>>

    suspend fun getPublicCollections(page: Int, pageSize: Int, userId: Long): UseCaseResult<BasicList<CollectionData>>

    suspend fun getPointCollections(page: Int, pageSize: Int, userId: Long, pointId: Long): UseCaseResult<BasicList<CollectionPointData>>

    suspend fun addPlaceToCollection(collectionId: Long, pointId: Long): UseCaseResult<Unit>

    suspend fun removePlaceFromCollection(collectionId: Long, pointId: Long): UseCaseResult<Unit>
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

    override suspend fun getUserCollections(page: Int, pageSize: Int, userId: Long): UseCaseResult<BasicList<CollectionData>> {
        return try {
            val result = collectionsApi.getUserCollections(pageSize, pageSize * page, userId)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun getPublicCollections(page: Int, pageSize: Int, userId: Long): UseCaseResult<BasicList<CollectionData>> {
        return try {
            val result = collectionsApi.getPublicCollections(pageSize, pageSize * page, userId)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun getPointCollections(page: Int, pageSize: Int, userId: Long, pointId: Long): UseCaseResult<BasicList<CollectionPointData>> {
        return try {
            val result = collectionsApi.getPointCollections(pageSize, pageSize * page, userId, pointId)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun addPlaceToCollection(collectionId: Long, pointId: Long): UseCaseResult<Unit> {
        return try {
            collectionsApi.addPlaceToCollection(collectionId, PlacesToModifyData(arrayOf(pointId)))
            UseCaseResult.Success(Unit)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun removePlaceFromCollection(collectionId: Long, pointId: Long): UseCaseResult<Unit> {
        return try {
            collectionsApi.removePlaceFromCollection(collectionId, PlacesToModifyData(arrayOf(pointId)))
            UseCaseResult.Success(Unit)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
