package com.coffilation.app.models

/**
 * @author pvl-zolotov on 28.11.2022
 */
data class PointData(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val displayName: String,
    val category: String,
    val type: String,
    val osmType: String,
    val osmId: Long,
)
