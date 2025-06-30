package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import com.vadhara7.mentorship_tree.domain.model.UserDto

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val mentorEmail: String = "",
    val requests: List<RequestUi> = emptyList()
) : State {
    data class RequestUi(
        val menteeId: String,
        val email: String,
        val name: String
    )
}

sealed interface HomeIntent : Intent {
    data object Init : HomeIntent
    data object OnSignOutClick : HomeIntent
    data object OnSendRequestClick : HomeIntent
    data class OnApproveRequest(val request: HomeState.RequestUi) : HomeIntent
    data class OnRejectRequest(val request: HomeState.RequestUi) : HomeIntent
    data class OnMentorEmailChange(val email: String) : HomeIntent
}

sealed interface HomeEffect : Effect {
    data class OnUserUpdate(val user: UserDto) : HomeEffect
    data class OnRequestUpdate(val requests: List<HomeState.RequestUi>) : HomeEffect
    data class OnMentorEmailChange(val email: String) : HomeEffect
}

sealed interface HomeEvent : Event