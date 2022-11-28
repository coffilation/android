package com.coffilation.app.data

/**
 * @author pvl-zolotov on 28.11.2022
 */
data class PointData(
    val id: Long,
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val displayName: String,
    val category: String,
    val type: String,
)
