package com.coffilation.app.models

/**
 * @author pvl-zolotov on 28.11.2022
 */
data class PointData(
    val id: Long,
    val address: HashMap<String, String>,
    val lat: Double,
    val lon: Double,
    val displayName: String,
    val category: String,
    val type: String,
    val osmType: String,
    val osmId: Long,
) {

    fun getName(): String {
        return displayName.split(", ")[0]
    }

    fun getAddress(): String {
       return displayName.split(", ").drop(1).joinToString(", ")
    }
}
