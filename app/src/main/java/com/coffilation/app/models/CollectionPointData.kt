package com.coffilation.app.models

/**
 * @author pvl-zolotov on 15.05.2023
 */
class CollectionPointData(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: UserData,
    val isPrivate: Boolean,
    val primaryColor: String?,
    val secondaryColor: String?,
    var isPlaceIncluded: Boolean,
    val canChangePlacesList: Boolean,
) {

    fun getGradientData(): GradientData? {
        return if (!primaryColor.isNullOrEmpty() && !secondaryColor.isNullOrEmpty()) {
            GradientData(primaryColor, secondaryColor)
        } else {
            null
        }
    }
}
