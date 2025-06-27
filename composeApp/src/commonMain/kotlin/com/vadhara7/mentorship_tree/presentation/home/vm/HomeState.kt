package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = ""
) : State

sealed interface HomeIntent : Intent {
    data object Init : HomeIntent
}

sealed interface HomeEffect : Effect {

}

sealed interface HomeEvent : Event