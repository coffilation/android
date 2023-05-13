package com.coffilation.app.domain

import com.coffilation.app.models.BasicList
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.util.domain.StateMachine
import com.coffilation.app.util.domain.stateMachineConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

/**
 * @author pvl-zolotov on 11.05.2023
 */
class BasicStateInteractor<T, R>(
    private val loadingEvent: suspend (page: Int, pageSize: Int, args: R) -> UseCaseResult<BasicList<T>>
) {

    fun getModel(externalScope: CoroutineScope, pageSize: Int): BasicModel<T, R> {
        val config = stateMachineConfig<BasicState<T>, Event, Effect>(
            initialState = BasicState.Initial()
        ) {
            onEvent<Event.RefreshRequested<R>> {
                inState<BasicState<T>> { _, event ->
                    result(BasicState.Loading(), Effect.LoadRequest(0, event.args))
                }
                inOtherStateSkip()
            }
            onEvent<Event.Loaded<T>> {
                inState<BasicState.Loading<T>> { state, event ->
                    result(BasicState.create(state, event.data))
                }
            }
            onEvent<Event.LoadNextPageRequested<R>> {
                inState<BasicState.CanLoadMore<T>> { state, event ->
                    val newState = BasicState.Loading(
                        state.currentPage + 1,
                        state.data,
                    )
                    result(newState, Effect.LoadRequest(newState.currentPage, event.args))
                }
            }
            onEvent<Event.LoadingFailed> {
                inState<BasicState.Loading<T>> { state, event ->
                    result(BasicState.LoadingError(state.currentPage, event.error, state.data))
                }
            }
            onEvent<Event.RetryRequested<R>> {
                inState<BasicState.LoadingError<T>> { state, event ->
                    result(
                        BasicState.Loading(state.currentPage, state.data),
                        Effect.LoadRequest(state.currentPage, event.args)
                    )
                }
            }
        }

        val loadingEvents = config.effects
            .filterIsInstance<Effect.LoadRequest<R>>()
            .mapLatest { effect ->
                val result = loadingEvent.invoke(effect.page, pageSize, effect.args)
                when (result) {
                    is UseCaseResult.Success -> Event.Loaded(result.data)
                    is UseCaseResult.Error -> Event.LoadingFailed(result.exception)
                }
            }

        val stateMachine = StateMachine.create(externalScope, config, loadingEvents)

        return BasicModel(
            state = stateMachine.state,
            nextPage = { args: R -> stateMachine.send(Event.LoadNextPageRequested(args)) },
            retry = { args: R -> stateMachine.send(Event.RetryRequested(args)) },
            refresh = { args: R -> stateMachine.send(Event.RefreshRequested(args)) },
        )
    }

    sealed interface Event {

        class RefreshRequested<R>(val args: R) : Event

        class Loaded<T>(val data: BasicList<T>) : Event

        class LoadingFailed(val error: Throwable) : Event

        class LoadNextPageRequested<R>(val args: R) : Event

        class RetryRequested<R>(val args: R) : Event
    }

    sealed class Effect {

        class LoadRequest<R>(val page: Int, val args: R) : Effect()
    }
}
