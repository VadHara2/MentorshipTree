package com.vadhara7.mentorship_tree.presentation.notification.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetRequestsUseCase
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationEffect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationViewModel(
    processor: NotificationProcessor,
    reducer: NotificationReducer
) : MviViewModel<NotificationIntent, NotificationEffect, NotificationEvent, NotificationState>(
    defaultState = NotificationState(),
    processor = processor,
    reducer = reducer
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
                    // todo show success snackbar
                }
                if (result.isFailure) {
                    Logger.e("NotificationIntent.AcceptRequest: isFailure")
                    // todo show fail snackbar
                }
            }

            is NotificationIntent.DeclineRequest -> flow {
                val result = relationsRepository.rejectRequest(intent.userId)
                if (result.isSuccess) {
                    Logger.i("NotificationIntent.DeclineRequest: isSuccess")
                    // todo show success snackbar
                }
                if (result.isFailure) {
                    Logger.e("NotificationIntent.DeclineRequest: isFailure")
                    // todo show fail snackbar
                }
            }
        }
    }
}

class NotificationReducer : Reducer<NotificationEffect, NotificationState> {
    override fun reduce(effect: NotificationEffect, state: NotificationState): NotificationState? {
        return when (effect) {
            is NotificationEffect.OnRequestsUpdate -> state.copy(requests = effect.requests)
        }
    }
}