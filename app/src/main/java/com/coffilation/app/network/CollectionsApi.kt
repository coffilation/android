package com.coffilation.app.network

import com.coffilation.app.data.CollectionAddData
import com.coffilation.app.data.CollectionAddResult
import com.coffilation.app.data.CollectionData
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.data.UserSignUpResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author pvl-zolotov on 17.11.2022
 */
interface CollectionsApi {

    @POST("collections")
    suspend fun addCollection(
        @Body collectionAddData: CollectionAddData
    ): CollectionAddResult

    @GET("collections")
    suspend fun getUserCollections(
        @Query("userId") userId: Long
    ): List<CollectionData>

    @GET("collections")
    suspend fun getPublicCollections(): List<CollectionData>
}
