package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import com.vadhara7.mentorship_tree.domain.model.UserDto

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = ""
) : State

sealed interface HomeIntent : Intent {
    data object Init : HomeIntent
    data object OnSignOutClick : HomeIntent
}

sealed interface HomeEffect : Effect {
    data class OnUserUpdate(val user: UserDto) : HomeEffect
}

sealed interface HomeEvent : Event