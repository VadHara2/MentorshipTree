package com.vadhara7.mentorship_tree.domain.model.ui

import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode
import kotlinx.serialization.Serializable

@Serializable
data class MentorshipTree(
    val mentors: List<RelationNode>,
    val mentees: List<RelationNode>
)