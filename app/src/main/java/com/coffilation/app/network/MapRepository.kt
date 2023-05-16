package com.coffilation.app.network

import com.coffilation.app.models.PointData
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 16.05.2023
 */
interface MapRepository {

    suspend fun getPointsForCollections(collectionIds: Array<Long>): UseCaseResult<List<PointData>>
}

class MapRepositoryImpl(private val mapApi: MapApi) : MapRepository {

    override suspend fun getPointsForCollections(collectionIds: Array<Long>): UseCaseResult<List<PointData>> {
        return try {
            val result = mapApi.getPointsForCollections(collectionIds)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
