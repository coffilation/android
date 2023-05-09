package com.coffilation.app.util.domain

import com.coffilation.app.util.wrongTypeMessage

/**
 * @author pvl-zolotov on 09.05.2023
 */
typealias EventHandler<State, Event, Effect> =
        (State, Event) -> SideEffectsEventHandler.Result<State, Effect>

class SideEffectsEventHandler<State : Any, Event : Any, Effect : Any> private constructor(
    private val stateHandlers: Map<Class<out State>, EventHandler<State, Event, Effect>>,
    private val strict: Boolean
) {

    fun handle(state: State, event: Event): Result<State, Effect> {
        val handler = stateHandlers[state::class.java]
            ?: run {
                stateHandlers.entries.firstOrNull { entry ->
                    entry.key.isAssignableFrom(state::class.java)
                }?.value
            }

        return if (handler != null) {
            handler.invoke(state, event)
        } else if (strict) {
            throw MissingEventHandlerException(
                "Handler for ${event::class.java.simpleName} event " +
                    "in ${state::class.java.simpleName} state is missing"
            )
        } else {
            Result(state)
        }
    }

    class Builder<State : Any, Event : Any, Effect : Any> {

        val stateHandlers = HashMap<Class<out State>, EventHandler<State, Event, Effect>>()
        private var strict: Boolean = true

        inline fun <reified S : State> inState(
            crossinline handler: (S, Event) -> Result<State, Effect>
        ) {
            check(stateHandlers[S::class.java] == null) {
                "Handler for ${S::class.java.simpleName} state already exists"
            }
            stateHandlers[S::class.java] = { state: State, event: Event ->
                check(state is S) { wrongTypeMessage<S>(state) }
                handler.invoke(state, event)
            }
        }

        fun inOtherStateSkip() {
            strict = false
        }

        fun build(): SideEffectsEventHandler<State, Event, Effect> {
            return SideEffectsEventHandler(stateHandlers.toMap(), strict)
        }

        fun result(state: State, effect: Effect? = null): Result<State, Effect> {
            return Result(state, effect)
        }
    }

    class Result<State : Any, Effect : Any>(val state: State, val effect: Effect? = null)
}
