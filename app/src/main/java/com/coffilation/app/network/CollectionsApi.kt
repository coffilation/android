package com.coffilation.app.network

import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.CollectionAddResult
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionList
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author pvl-zolotov on 17.11.2022
 */
interface CollectionsApi {

    @POST("compilations/")
    suspend fun addCollection(
        @Body collectionAddData: CollectionAddData
    ): CollectionAddResult

    @GET("compilations/")
    suspend fun getUserCollections(
        @Query("compilationmembership_user") userId: Long
    ): List<CollectionData>

    @GET("compilations/")
    suspend fun getPublicCollections(
        @Query("limit") pageSize: Int,
        @Query("offset") offset: Int,
    ): CollectionList
}
