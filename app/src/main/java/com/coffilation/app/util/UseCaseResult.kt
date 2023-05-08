package com.coffilation.app.util

/**
 * @author pvl-zolotov on 15.10.2022
 */
sealed class UseCaseResult<out T : Any> {
    class Success<out T : Any>(val data: T) : UseCaseResult<T>()
    class Error(val exception: Throwable, val message: Array<String>? = null) : UseCaseResult<Nothing>()
}
