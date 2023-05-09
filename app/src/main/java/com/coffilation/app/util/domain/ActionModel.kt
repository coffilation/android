package com.coffilation.app.util.domain

import com.coffilation.app.util.domain.ActionModel.Event
import com.coffilation.app.util.domain.ActionModel.PerformActionEffect
import com.coffilation.app.util.runSuspendCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @author pvl-zolotov on 09.05.2023
 */
class ActionModel<T, R>(
    val state: StateFlow<ActionState<T, R>>,
    val action: (T) -> Unit,
    val retry: () -> Unit
) {

    sealed class Event<T, R> {

        class PerformRequested<T, R>(val params: T) : Event<T, R>()

        class Performed<T, R>(val params: T, val data: R) : Event<T, R>()

        class PerformFailed<T, R>(val params: T, val error: Throwable) : Event<T, R>()

        class RetryRequested<T, R> : Event<T, R>()
    }

    class PerformActionEffect<T>(val params: T)
}

fun <T, R> actionModel(
    externalScope: CoroutineScope,
    cancelable: Boolean = false,
    action: suspend (T) -> R
): ActionModel<T, R> {
    val config = stateMachineConfig<ActionState<T, R>, Event<T, R>, PerformActionEffect<T>>(
        ActionState.Initial()
    ) {
        onEvent<Event.PerformRequested<T, R>> {
            inState<ActionState.Initial<T, R>> { _, event ->
                result(
                    ActionState.Process(event.params),
                    PerformActionEffect(event.params)
                )
            }
            inState<ActionState.Error<T, R>> { _, event ->
                result(
                    ActionState.Process(event.params),
                    PerformActionEffect(event.params)
                )
            }
            inState<ActionState.Success<T, R>> { _, event ->
                result(
                    ActionState.Process(event.params),
                    PerformActionEffect(event.params)
                )
            }
            if (cancelable) {
                inState<ActionState.Process<T, R>> { state, event ->
                    if (!state.isForParams(event.params)) {
                        result(
                            ActionState.Process(event.params),
                            PerformActionEffect(event.params)
                        )
                    } else {
                        result(state)
                    }
                }
            } else {
                inOtherStateSkip()
            }
        }
        onEvent<Event.Performed<T, R>> {
            inState<ActionState.Process<T, R>> { state, event ->
                if (state.isForParams(event.params)) {
                    result(ActionState.Success(state.params, event.data))
                } else {
                    result(state)
                }
            }
        }
        onEvent<Event.PerformFailed<T, R>> {
            inState<ActionState.Process<T, R>> { state, event ->
                if (state.isForParams(event.params)) {
                    result(ActionState.Error(state.params, event.error))
                } else {
                    result(state)
                }
            }
        }
        onEvent<Event.RetryRequested<T, R>> {
            inState<ActionState.Error<T, R>> { state, _ ->
                result(
                    ActionState.Process(state.params),
                    PerformActionEffect(state.params)
                )
            }
            inOtherStateSkip()
        }
    }

    val performingEvents = config.effects
        .flatMapLatest { effect ->
            flow {
                val event = runSuspendCatching { action.invoke(effect.params) }
                    .fold(
                        onSuccess = { Event.Performed(effect.params, it) },
                        onFailure = { Event.PerformFailed(effect.params, it) }
                    )
                emit(event)
            }
        }

    val stateMachine = StateMachine.create(externalScope, config, performingEvents)

    return ActionModel(
        state = stateMachine.state,
        action = { params ->
            stateMachine.send(Event.PerformRequested(params))
        },
        retry = { stateMachine.send(Event.RetryRequested()) }
    )
}

fun <T, R> actionModelLazy(
    externalScope: CoroutineScope,
    cancelable: Boolean = false,
    action: suspend (T) -> R
): ActionModel<T, R> {
    val state = MutableStateFlow<ActionState<T, R>>(ActionState.Initial())
    val wrapped by lazy {
        actionModel(externalScope, cancelable, action)
            .also { model ->
                model.state
                    .onEach { state.value = it }
                    .launchIn(externalScope)
            }
    }
    return ActionModel(
        state = state,
        action = { wrapped.action.invoke(it) },
        retry = { wrapped.retry.invoke() }
    )
}
