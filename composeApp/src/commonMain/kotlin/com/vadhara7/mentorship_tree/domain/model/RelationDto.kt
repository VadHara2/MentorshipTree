package com.vadhara7.mentorship_tree.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RelationType {
    @SerialName("MENTOR") MENTOR,
    @SerialName("MENTEE") MENTEE
}

@Serializable
data class RelationDto(
    val userUid: String,
    val type: RelationType,
    val since: Long
)