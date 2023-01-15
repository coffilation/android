package com.coffilation.app.data

import com.yandex.mapkit.geometry.BoundingBox

/**
 * @author pvl-zolotov on 29.11.2022
 */
data class SearchData(
    val viewbox: String,
    val query: String
) {

    companion object {

        fun newInstance(boundingBox: BoundingBox, query: String): SearchData {
            return SearchData(
                "${boundingBox.northEast.longitude},${boundingBox.northEast.latitude},${boundingBox.southWest.longitude},${boundingBox.southWest.latitude}",
                query
            )
        }
    }
}
