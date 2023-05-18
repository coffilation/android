package com.coffilation.app.network

import com.coffilation.app.models.CollectionPermissions
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author pvl-zolotov on 18.05.2023
 */
interface CollectionPermissionsApi {

    @GET("compilation_permissions/{collectionId}/{userId}/")
    suspend fun getCollectionPermissions(
        @Path("collectionId") collectionId: Long,
        @Path("userId") userId: Long,
    ): Array<CollectionPermissions>
}
