package com.coffilation.app.util

import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.UserData

/**
 * @author pvl-zolotov on 08.05.2023
 */
fun Array<String>.format(): String {
    return joinToString(",\n")
}

inline fun <reified Expected> wrongTypeMessage(value: Any): String {
    return "Expected ${Expected::class.java.simpleName} but was ${value.javaClass.simpleName}"
}

fun List<CollectionData>.filterNonOwnCollections(user: UserData?): List<CollectionData> {
    return filter { it.owner.id != user?.id }
}

const val PAGE_SIZE = 10
