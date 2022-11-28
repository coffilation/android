package com.coffilation.app.data

/**
 * @author pvl-zolotov on 21.11.2022
 */
data class CollectionAddData(
    val name: String,
    val description: String,
    val type: CollectionType,
    val gradient: GradientData
)
