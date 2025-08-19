package com.vadhara7.mentorship_tree.presentation.notification.vm

import com.vadhara7.mentorship_tree.core.mvi.*
import com.vadhara7.mentorship_tree.domain.model.ui.RequestUi

data class NotificationState(
    val requests: List<RequestUi> = emptyList()
) : State

sealed interface NotificationIntent : Intent {
    data object Init : NotificationIntent
    data class AcceptRequest(val userId: String) : NotificationIntent
    data class DeclineRequest(val userId: String) : NotificationIntent
}

sealed interface NotificationEffect : Effect {
    data class OnRequestsUpdate(val requests: List<RequestUi>) : NotificationEffect
    data class OnAcceptRequestResult(val userId: String, val isSuccess: Boolean) : NotificationEffect
    data class OnDeclineRequestResult(val userId: String, val isSuccess: Boolean) : NotificationEffect
}

sealed interface NotificationEvent : Event {
    data class ShowAcceptSuccess(val userId: String) : NotificationEvent
    data object ShowAcceptFailure : NotificationEvent
    data class ShowDeclineSuccess(val userId: String) : NotificationEvent
    data object ShowDeclineFailure : NotificationEvent
}

