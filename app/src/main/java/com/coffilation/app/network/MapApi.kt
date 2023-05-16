package com.coffilation.app.network

import com.coffilation.app.models.PointData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author pvl-zolotov on 16.05.2023
 */
interface MapApi {

    @GET("/map/places/")
    suspend fun getPointsForCollections(
        @Query("compilation") collectionIds: Array<Long>
    ): List<PointData>
}
