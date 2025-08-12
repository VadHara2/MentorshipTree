package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import com.vadhara7.mentorship_tree.domain.model.ui.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val mentorshipTree: MentorshipTree = MentorshipTree(
        mentors = emptyList(),
        mentees = emptyList()
    ),
    val showAddMentorByEmailDialog: Boolean = false
) : State

sealed interface HomeIntent : Intent {
    data object Init : HomeIntent
    data object OnAddMentorClick : HomeIntent
    data class AddMentorByEmail(val email: String, val message: String) : HomeIntent
    data object OnCloseDialogClick : HomeIntent
}

sealed interface HomeEffect : Effect {
    data class OnUserUpdate(val user: UserDto) : HomeEffect
    data class OnMentorshipTreeUpdate(val mentorshipTree: MentorshipTree) : HomeEffect
    data class UpdateAddMentorDialog(val isShow: Boolean) : HomeEffect
}

sealed interface HomeEvent : Event {
//    data object ShowAddMentorByEmailDialog : HomeEvent
}