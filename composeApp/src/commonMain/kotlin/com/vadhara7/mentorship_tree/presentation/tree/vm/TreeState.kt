package com.vadhara7.mentorship_tree.presentation.tree.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import com.vadhara7.mentorship_tree.domain.model.ui.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode

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
    data object OnAddMenteeClick : TreeIntent
    data class OnDeleteRelation(val relation: RelationNode) : TreeIntent
    data class OnRestoreRelation(val relation: RelationNode) : TreeIntent
    data class OnSendRestoreRequest(val relation: RelationNode) : TreeIntent
}

sealed interface TreeEffect : Effect {
    data class OnUserUpdate(val user: UserDto) : TreeEffect
    data class OnMentorshipTreeUpdate(val mentorshipTree: MentorshipTree) : TreeEffect
    data class OnRelationDeletedResponse(
        val isSuccess: Boolean,
        val relation: RelationNode
    ) : TreeEffect

    data class OnRelationRestoreResponse(
        val isSuccess: Boolean,
        val relation: RelationNode
    ) : TreeEffect
}

sealed interface TreeEvent : Event {
    data class ShowFailDeleteSnackbar(val relation: RelationNode) : TreeEvent
    data class ShowSuccessDeleteSnackbarWithCancelAction(
        val relation: RelationNode
    ) : TreeEvent
    data class ShowFailRestoreSnackbar(val relation: RelationNode) : TreeEvent

}