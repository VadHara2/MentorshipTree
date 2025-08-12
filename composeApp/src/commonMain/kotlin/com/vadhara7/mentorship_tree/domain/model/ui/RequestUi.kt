package com.vadhara7.mentorship_tree.domain.model.ui

import com.vadhara7.mentorship_tree.domain.model.dto.RequestStatus
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto

data class RequestUi(
    val fromUser: UserDto,
    val status: RequestStatus,
    val createdAt: Long,
    val reviewedAt: Long?,
    val message: String?
)