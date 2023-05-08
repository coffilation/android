package com.coffilation.app.util

/**
 * @author pvl-zolotov on 08.05.2023
 */
fun Array<String>.format(): String {
    return joinToString(",\n")
}
