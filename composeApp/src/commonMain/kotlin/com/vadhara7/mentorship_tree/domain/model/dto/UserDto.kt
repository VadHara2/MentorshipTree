package com.vadhara7.mentorship_tree.domain.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String,
    val displayName: String?,
    val email: String,
    val createdAt: Long
)