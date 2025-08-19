package com.vadhara7.mentorship_tree.presentation.notification.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.core.mvi.Publisher
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetRequestsUseCase
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationEffect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationViewModel(
    processor: NotificationProcessor,
    reducer: NotificationReducer,
    publisher: NotificationPublisher
) : MviViewModel<NotificationIntent, NotificationEffect, NotificationEvent, NotificationState>(
    defaultState = NotificationState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher
) {
    init {
        process(NotificationIntent.Init)
    }
}

class NotificationProcessor(
    private val relationsRepository: RelationsRepository,
    private val getRequestsUseCase: GetRequestsUseCase
) :
    Processor<NotificationIntent, NotificationEffect, NotificationState> {
    override fun process(
        intent: NotificationIntent,
        state: NotificationState
    ): Flow<NotificationEffect> {
        return when (intent) {
            NotificationIntent.Init -> flow {
                getRequestsUseCase().collect {
                    emit(OnRequestsUpdate(it))
                }
            }

            is NotificationIntent.AcceptRequest -> flow {
                val result = relationsRepository.approveRequest(intent.userId)
                if (result.isSuccess) {
                    Logger.i("NotificationIntent.AcceptRequest: isSuccess")
                }
                if (result.isFailure) {
                    Logger.e("NotificationIntent.AcceptRequest: isFailure")
                }
                emit(OnAcceptRequestResult(result.isSuccess))
            }

            is NotificationIntent.DeclineRequest -> flow {
                val result = relationsRepository.rejectRequest(intent.userId)
                if (result.isSuccess) {
                    Logger.i("NotificationIntent.DeclineRequest: isSuccess")
                }
                if (result.isFailure) {
                    Logger.e("NotificationIntent.DeclineRequest: isFailure")
                }
                emit(OnDeclineRequestResult(result.isSuccess))
            }
        }
    }
}

class NotificationReducer : Reducer<NotificationEffect, NotificationState> {
    override fun reduce(effect: NotificationEffect, state: NotificationState): NotificationState? {
        return when (effect) {
            is NotificationEffect.OnRequestsUpdate -> state.copy(requests = effect.requests)
            is NotificationEffect.OnAcceptRequestResult -> state
            is NotificationEffect.OnDeclineRequestResult -> state
        }
    }
}

class NotificationPublisher : Publisher<NotificationEffect, NotificationEvent> {
    override fun publish(effect: NotificationEffect): NotificationEvent? {
        return when (effect) {
            is NotificationEffect.OnAcceptRequestResult -> if (effect.isSuccess) {
                NotificationEvent.ShowAcceptSuccess
            } else {
                NotificationEvent.ShowAcceptFailure
            }
            is NotificationEffect.OnDeclineRequestResult -> if (effect.isSuccess) {
                NotificationEvent.ShowDeclineSuccess
            } else {
                NotificationEvent.ShowDeclineFailure
            }
            is NotificationEffect.OnRequestsUpdate -> null
        }
    }
}
