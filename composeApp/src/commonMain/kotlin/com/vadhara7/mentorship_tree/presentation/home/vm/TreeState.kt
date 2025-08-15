package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import com.vadhara7.mentorship_tree.domain.model.ui.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto

data class TreeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val mentorshipTree: MentorshipTree = MentorshipTree(
        mentors = emptyList(),
        mentees = emptyList()
    )
) : State

sealed interface TreeIntent : Intent {
    data object Init : TreeIntent
    data object OnAddMentorClick : TreeIntent
}

sealed interface TreeEffect : Effect {
    data class OnUserUpdate(val user: UserDto) : TreeEffect
    data class OnMentorshipTreeUpdate(val mentorshipTree: MentorshipTree) : TreeEffect
}

sealed interface TreeEvent : Event