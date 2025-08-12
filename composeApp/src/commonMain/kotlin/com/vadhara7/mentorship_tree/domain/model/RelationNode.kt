package com.vadhara7.mentorship_tree.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RelationNode(
    val userUid: UserDto,
    val type: RelationType,
    val since: Long,
    val children: List<RelationNode> = emptyList()
)
