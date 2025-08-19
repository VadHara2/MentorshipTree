package com.vadhara7.mentorship_tree.presentation.addMentor.vm

import com.vadhara7.mentorship_tree.core.mvi.*

data class AddMentorState(
    val email: String = "",
    val message: String = "",
    val isEmailValid: Boolean = false
) : State

sealed interface AddMentorIntent : Intent {
    data class OnEmailInput(val input: String) : AddMentorIntent
    data class OnMessageInput(val input: String) : AddMentorIntent
    data object OnSendRequestClick : AddMentorIntent
    data object OnCloseClick : AddMentorIntent
    data object OnScanQrClick : AddMentorIntent
}

sealed interface AddMentorEffect : Effect {
    data class UpdateEmail(val email: String) : AddMentorEffect
    data class UpdateMessage(val message: String) : AddMentorEffect
    data object RequestSent : AddMentorEffect
    data object RequestUnsent : AddMentorEffect
    data object OnScanQrClick : AddMentorEffect
}

sealed interface AddMentorEvent : Event {
    data object CloseScreen : AddMentorEvent
    data object ShowRequestUnsent : AddMentorEvent
    data object OpenQrScanner : AddMentorEvent
}