package com.vadhara7.mentorship_tree.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MentorshipTree(
    val mentors: List<RelationNode>,
    val mentees: List<RelationNode>
)
