package com.vadhara7.mentorship_tree.presentation.addMentor.vm

import com.vadhara7.mentorship_tree.core.mvi.*
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddMentorViewModel(
    processor: AddMentorProcessor,
    reducer: AddMentorReducer,
    publisher: AddMentorPublisher
) : MviViewModel<AddMentorIntent, AddMentorEffect, AddMentorEvent, AddMentorState>(
    defaultState = AddMentorState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher
)

class AddMentorProcessor(
    private val relationsRepository: RelationsRepository,
) :
    Processor<AddMentorIntent, AddMentorEffect, AddMentorState> {
    override fun process(
        intent: AddMentorIntent,
        state: AddMentorState
    ): Flow<AddMentorEffect> {
        return when (intent) {
            is AddMentorIntent.OnCloseClick -> flow { }
            is AddMentorIntent.OnEmailInput -> flow {
                emit(AddMentorEffect.UpdateEmail(intent.input))
            }

            is AddMentorIntent.OnMessageInput -> flow {
                emit(AddMentorEffect.UpdateMessage(intent.input))
            }

            is AddMentorIntent.OnSendRequestClick -> flow {
                val response = relationsRepository.sendRequestToBecomeMentee(
                    mentorEmail = state.email,
                    message = state.message.ifEmpty { null }
                )
                if (response.isSuccess) emit(AddMentorEffect.RequestSent)
                if (response.isFailure) emit(AddMentorEffect.RequestUnsent)
            }
        }
    }
}

class AddMentorReducer :
    Reducer<AddMentorEffect, AddMentorState> {
    override fun reduce(
        effect: AddMentorEffect,
        state: AddMentorState,
    ): AddMentorState {
        return when (effect) {
            is AddMentorEffect.RequestSent -> state
            is AddMentorEffect.RequestUnsent -> state
            is AddMentorEffect.UpdateEmail -> {
                state.copy(
                    email = effect.email,
                    isEmailValid = effect.email.isValidEmail()
                )
            }
            is AddMentorEffect.UpdateMessage -> state.copy(message = effect.message)
        }
    }
}

class AddMentorPublisher : Publisher<AddMentorEffect, AddMentorEvent> {
    override fun publish(effect: AddMentorEffect): AddMentorEvent? {
        return when (effect) {
            is AddMentorEffect.RequestSent -> AddMentorEvent.CloseScreen
            is AddMentorEffect.RequestUnsent -> AddMentorEvent.ShowRequestUnsent
            is AddMentorEffect.UpdateEmail -> null
            is AddMentorEffect.UpdateMessage -> null
        }
    }

}

private fun String.isValidEmail(): Boolean {
    val s = trim()
    val at = s.indexOf('@')
    if (at <= 0 || at == s.lastIndex) return false
    val dot = s.indexOf('.', startIndex = at + 2)
    return dot in (at + 2)..<s.lastIndex
}