package com.coffilation.app.models

/**
 * @author pvl-zolotov on 25.11.2022
 */
data class CollectionData(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: UserData,
    val isPrivate: Boolean,
    val primaryColor: String?,
    val secondaryColor: String?,
) {

    fun getGradientData(): GradientData? {
        return if (!primaryColor.isNullOrEmpty() && !secondaryColor.isNullOrEmpty()) {
            GradientData(primaryColor, secondaryColor)
        } else {
            null
        }
    }
}
