package com.coffilation.app.util.domain

/**
 * @author pvl-zolotov on 09.05.2023
 */
sealed class ActionState<T, R> {

    abstract fun isForParams(params: T): Boolean

    class Initial<T, R> : ActionState<T, R>() {

        override fun isForParams(params: T): Boolean {
            return false
        }
    }

    class Process<T, R>(val params: T) : ActionState<T, R>() {

        override fun isForParams(params: T): Boolean {
            return this.params == params
        }
    }

    class Error<T, R>(val params: T, val error: Throwable) : ActionState<T, R>() {

        override fun isForParams(params: T): Boolean {
            return this.params == params
        }
    }

    class Success<T, R>(val params: T, val data: R) : ActionState<T, R>() {

        override fun isForParams(params: T): Boolean {
            return this.params == params
        }
    }
}
