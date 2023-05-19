package com.coffilation.app.network

import com.coffilation.app.models.CollectionPermissions

/**
 * @author pvl-zolotov on 18.05.2023
 */
interface CollectionPermissionsRepository {

    suspend fun getCollectionPermissions(collectionId: Long, userId: Long): Array<CollectionPermissions>
}

class CollectionPermissionsRepositoryImpl(private val collectionPermissionsApi: CollectionPermissionsApi) : CollectionPermissionsRepository{

    override suspend fun getCollectionPermissions(collectionId: Long, userId: Long): Array<CollectionPermissions> {
        return collectionPermissionsApi.getCollectionPermissions(collectionId, userId)
    }
}
