package com.vadhara7.mentorship_tree.domain.model.ui

import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
import kotlinx.serialization.Serializable

@Serializable
data class RelationNode(
    val user: UserDto,
    val type: RelationType,
    val since: Long,
    val children: List<RelationNode> = emptyList()
)