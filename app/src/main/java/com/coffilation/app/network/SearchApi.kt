package com.coffilation.app.network

import com.coffilation.app.models.PointData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author pvl-zolotov on 29.11.2022
 */
interface SearchApi {

    @GET("/nominatim/search/")
    suspend fun search(
        @Query("viewbox") viewbox: String,
        @Query("q") query: String
    ): List<PointData>
}
