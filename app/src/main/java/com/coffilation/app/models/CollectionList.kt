package com.coffilation.app.models

/**
 * @author pvl-zolotov on 09.05.2023
 */
class CollectionList(val count: Int, val results: List<CollectionData>) {
    init {
        require(count >= 0) { "totalCount is negative" }
    }
}
