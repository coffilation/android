package com.coffilation.app.util

/**
 * @author pvl-zolotov on 08.05.2023
 */
fun Array<String>.format(): String {
    return joinToString(",\n")
}

inline fun <reified Expected> wrongTypeMessage(value: Any): String {
    return "Expected ${Expected::class.java.simpleName} but was ${value.javaClass.simpleName}"
}

fun <T> List<T>.elementEquals(list: List<T>, predicate: (Pair<T, T>) -> Boolean): Boolean {
    if (size != list.size) {
        return false
    }
    val pairList = zip(list)
    return pairList.all(predicate)
}

const val PAGE_SIZE = 10
