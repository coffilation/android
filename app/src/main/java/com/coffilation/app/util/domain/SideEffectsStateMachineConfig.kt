package com.coffilation.app.util.domain

import com.coffilation.app.util.wrongTypeMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer

/**
 * @author pvl-zolotov on 09.05.2023
 */
class SideEffectsStateMachineConfig<State : Any, Event : Any, Effect : Any> private constructor(
    override val dispatcher: StateDispatcher,
    override val initialState: State,
    private val eventHandlers: Map<Class<out Event>, EventHandler<State, Event, Effect>>
) : StateMachine.Config<State, Event> {

    private val effectsFlow = MutableSharedFlow<Effect>()

    val effects: Flow<Effect> = effectsFlow.buffer()

    override suspend fun handleEvent(state: State, event: Event): State {
        val handler = checkNotNull(eventHandlers[event::class.java]) {
            "Handler for ${event::class.java.simpleName} event is missing"
        }
        val result = handler.invoke(state, event)
        if (result.effect != null) {
            effectsFlow.emit(result.effect)
        }
        return result.state
    }

    class Builder<State : Any, Event : Any, Effect : Any>(
        private val dispatcher: StateDispatcher,
        private val initialState: State
    ) {

        val eventHandlers =
            HashMap<Class<out Event>, EventHandler<State, Event, Effect>>()

        inline fun <reified E : Event> onEvent(
            init: SideEffectsEventHandler.Builder<State, E, Effect>.() -> Unit
        ) {
            check(eventHandlers[E::class.java] == null) {
                "Handler for ${E::class.java.simpleName} event already exists"
            }
            val handler = SideEffectsEventHandler.Builder<State, E, Effect>().apply(init).build()
            eventHandlers[E::class.java] = { state: State, event: Event ->
                check(event is E) { wrongTypeMessage<E>(event) }
                handler.handle(state, event)
            }
        }

        fun build(): SideEffectsStateMachineConfig<State, Event, Effect> {
            return SideEffectsStateMachineConfig(
                dispatcher,
                initialState,
                eventHandlers.toMap()
            )
        }
    }
}

fun <State : Any, Event : Any, Effect : Any> stateMachineConfig(
    initialState: State,
    dispatcher: StateDispatcher = StateDispatcher.default,
    init: SideEffectsStateMachineConfig.Builder<State, Event, Effect>.() -> Unit
): SideEffectsStateMachineConfig<State, Event, Effect> {
    return SideEffectsStateMachineConfig.Builder<State, Event, Effect>(dispatcher, initialState)
        .apply(init)
        .build()
}
