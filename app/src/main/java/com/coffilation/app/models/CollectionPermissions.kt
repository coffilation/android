package com.coffilation.app.models

import com.google.gson.annotations.SerializedName

/**
 * @author pvl-zolotov on 18.05.2023
 */
enum class CollectionPermissions(val value: String) {

    @SerializedName("compilation_change")
    COMPILATION_CHANGE("compilation_change"),

    @SerializedName("compilation_change_places_list")
    COMPILATION_CHANGE_POINT_LIST("compilation_change_places_list"),
}
