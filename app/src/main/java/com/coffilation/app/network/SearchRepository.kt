package com.coffilation.app.network

import com.coffilation.app.models.PointData
import com.coffilation.app.models.SearchData

/**
 * @author pvl-zolotov on 29.11.2022
 */
interface SearchRepository {

    suspend fun search(searchData: SearchData): List<PointData>
}

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override suspend fun search(searchData: SearchData): List<PointData> {
        return searchApi.search(searchData.viewbox, searchData.query)
    }
}
