package com.coffilation.app.network

import com.coffilation.app.data.PointData
import com.coffilation.app.data.SearchData
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 29.11.2022
 */
interface SearchRepository {

    suspend fun search(searchData: SearchData): UseCaseResult<List<PointData>>
}

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override suspend fun search(searchData: SearchData): UseCaseResult<List<PointData>> {
        return try {
            val result = searchApi.search(searchData.viewbox, searchData.query)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
