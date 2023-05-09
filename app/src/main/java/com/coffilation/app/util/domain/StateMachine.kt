package com.coffilation.app.util.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

/**
 * @author pvl-zolotov on 09.05.2023
 */
class StateMachine<State, Event> private constructor(
    val state: StateFlow<State>,
    val send: (Event) -> Unit,
) {

    companion object {

        fun <S, E> create(
            externalScope: CoroutineScope,
            config: Config<S, E>,
            vararg events: Flow<E>
        ): StateMachine<S, E> {
            val externalEvents = Channel<E>()
            val state = MutableStateFlow(config.initialState)

            externalScope.launch(config.dispatcher) {
                merge(externalEvents.receiveAsFlow(), *events)
                    .buffer()
                    .scan(config.initialState) { state, event ->
                        config.handleEvent(state, event)
                    }
                    .collect { state.value = it }
            }

            return StateMachine(state) { event ->
                externalScope.launch(config.dispatcher) {
                    externalEvents.send(event)
                }
            }
        }
    }

    interface Config<State, Event> {

        val dispatcher: StateDispatcher

        val initialState: State

        suspend fun handleEvent(state: State, event: Event): State
    }
}
