package com.coffilation.app.network

import com.coffilation.app.models.BasicList
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.CollectionAddResult
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.CollectionPointData
import com.coffilation.app.models.PlacesToModifyData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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
        @Query("limit") pageSize: Int,
        @Query("offset") offset: Int,
        @Query("compilationmembership_user") userId: Long,
    ): BasicList<CollectionData>

    @GET("compilations/?is_private=false")
    suspend fun getPublicCollections(
        @Query("limit") pageSize: Int,
        @Query("offset") offset: Int,
        @Query("compilationmembership__user__not") userId: Long,
    ): BasicList<CollectionData>

    @GET("compilations/populated_by_place/")
    suspend fun getPointCollections(
        @Query("limit") pageSize: Int,
        @Query("offset") offset: Int,
        @Query("compilationmembership__user") userId: Long,
        @Query("place") placeId: Long,
    ): BasicList<CollectionPointData>

    @POST("compilations/{id}/add_places/")
    suspend fun addPlaceToCollection(
        @Path("id") collectionId: Long,
        @Body placeIds: PlacesToModifyData,
    )

    @POST("compilations/{id}/remove_places/")
    suspend fun removePlaceFromCollection(
        @Path("id") collectionId: Long,
        @Body placeIds: PlacesToModifyData,
    )
}
