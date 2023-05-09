package com.coffilation.app.models

/**
 * @author pvl-zolotov on 25.11.2022
 */
data class CollectionData(
    val id: Long,
    val name: String,
    val description: String?,
    val author: UserData,
    val type: CollectionType,
    val gradient: GradientData?
)
