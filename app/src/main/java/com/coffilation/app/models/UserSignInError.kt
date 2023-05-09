package com.coffilation.app.models

/**
 * @author pvl-zolotov on 08.05.2023
 */
class UserSignInError(
    val username: Array<String>?,
    val password: Array<String>?,
) {

    fun getMessages(): Array<String> {
        return (username ?: emptyArray()) + (password ?: emptyArray())
    }
}
