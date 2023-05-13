package com.coffilation.app.models

/**
 * @author pvl-zolotov on 11.05.2023
 */
class BasicList<T>(val count: Int, val results: List<T>) {
    init {
        require(count >= 0) { "totalCount is negative" }
    }
}
