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
}

sealed interface NotificationEvent : Event