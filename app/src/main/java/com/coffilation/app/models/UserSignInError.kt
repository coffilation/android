package com.coffilation.app.models

/**
 * @author pvl-zolotov on 08.05.2023
 */
class UserSignInError(
    val username: Array<String>?,
    val password: Array<String>?,
    val detail: String?,
) {

    fun getMessages(): Array<String> {
        val formErrors = (username ?: emptyArray()) + (password ?: emptyArray())
        return formErrors.run {
            if (!detail.isNullOrEmpty()) {
                plus(detail)
            } else {
                this
            }
        }
    }
}
