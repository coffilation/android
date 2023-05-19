package com.coffilation.app.network

import com.coffilation.app.models.PointData

/**
 * @author pvl-zolotov on 16.05.2023
 */
interface MapRepository {

    suspend fun getPointsForCollections(collectionIds: Array<Long>): List<PointData>
}

class MapRepositoryImpl(private val mapApi: MapApi) : MapRepository {

    override suspend fun getPointsForCollections(collectionIds: Array<Long>): List<PointData> {
        return mapApi.getPointsForCollections(collectionIds)
    }
}
