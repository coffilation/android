package com.coffilation.app.network

import com.coffilation.app.models.CollectionPermissions
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 18.05.2023
 */
interface CollectionPermissionsRepository {

    suspend fun getCollectionPermissions(collectionId: Long, userId: Long): UseCaseResult<Array<CollectionPermissions>>
}

class CollectionPermissionsRepositoryImpl(private val collectionPermissionsApi: CollectionPermissionsApi) : CollectionPermissionsRepository{

    override suspend fun getCollectionPermissions(collectionId: Long, userId: Long): UseCaseResult<Array<CollectionPermissions>> {
        return try {
            val result = collectionPermissionsApi.getCollectionPermissions(collectionId, userId)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
