package com.coffilation.app.models

/**
 * @author pvl-zolotov on 21.11.2022
 */
data class CollectionAddData(
    val name: String,
    val description: String?,
    val isPrivate: Boolean,
    val primaryColor: String?,
    val secondaryColor: String?,
) {

    companion object {

        fun newInstance(
            name: String,
            description: String?,
            isPrivate: Boolean,
            gradientData: GradientData?
        ): CollectionAddData {
            return CollectionAddData(
                name,
                description,
                isPrivate,
                gradientData?.startColor,
                gradientData?.endColor
            )
        }
    }
}
