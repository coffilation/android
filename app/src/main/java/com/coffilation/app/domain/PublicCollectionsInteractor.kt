package com.coffilation.app.domain

import com.coffilation.app.models.CollectionList
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.util.domain.StateMachine
import com.coffilation.app.util.domain.stateMachineConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapLatest

/**
 * @author pvl-zolotov on 09.05.2023
 */
class PublicCollectionsInteractor(
    private val collectionsRepository: CollectionsRepository
) {

    fun getModel(externalScope: CoroutineScope, pageSize: Int): PublicCollectionsModel {
        val config = stateMachineConfig<PublicCollectionsState, Event, Effect>(
            initialState = PublicCollectionsState.Initial
        ) {
            onEvent<Event.RefreshRequested> {
                inState<PublicCollectionsState> { _, _ ->
                    result(PublicCollectionsState.Loading(), Effect.LoadRequest(0))
                }
                inOtherStateSkip()
            }
            onEvent<Event.Loaded> {
                inState<PublicCollectionsState.Loading> { state, event ->
                    result(PublicCollectionsState.create(state, event.collections))
                }
            }
            onEvent<Event.LoadNextPageRequested> {
                inState<PublicCollectionsState.CanLoadMore> { state, _ ->
                    val newState = PublicCollectionsState.Loading(
                        state.currentPage + 1,
                        state.collections,
                    )
                    result(newState, Effect.LoadRequest(newState.currentPage))
                }
            }
            onEvent<Event.LoadingFailed> {
                inState<PublicCollectionsState.Loading> { state, event ->
                    result(PublicCollectionsState.LoadingError(state.currentPage, event.error))
                }
            }
            onEvent<Event.RetryRequested> {
                inState<PublicCollectionsState.LoadingError> { state, _ ->
                    result(
                        PublicCollectionsState.Loading(state.currentPage, state.collections),
                        Effect.LoadRequest(state.currentPage)
                    )
                }
            }
        }

        val loadingEvents = config.effects
            .filterIsInstance<Effect.LoadRequest>()
            .mapLatest { effect ->
                val result = collectionsRepository.getPublicCollections(effect.page, pageSize)
                when (result) {
                    is UseCaseResult.Success -> Event.Loaded(result.data)
                    is UseCaseResult.Error -> Event.LoadingFailed(result.exception)
                }
            }

        val stateMachine = StateMachine.create(externalScope, config, loadingEvents)

        return PublicCollectionsModel(
            state = stateMachine.state,
            nextPage = { stateMachine.send(Event.LoadNextPageRequested) },
            retry = { stateMachine.send(Event.RetryRequested) },
            refresh = { stateMachine.send(Event.RefreshRequested) },
        )
    }

    sealed interface Event {

        object RefreshRequested : Event

        class Loaded(val collections: CollectionList) : Event

        class LoadingFailed(val error: Throwable) : Event

        object LoadNextPageRequested : Event

        object RetryRequested : Event
    }

    sealed class Effect {

        class LoadRequest(val page: Int) : Effect()
    }
}
