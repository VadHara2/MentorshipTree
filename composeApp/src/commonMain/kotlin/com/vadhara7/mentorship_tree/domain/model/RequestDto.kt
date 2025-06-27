package com.vadhara7.mentorship_tree.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class RequestStatus {
    @SerialName("pending") PENDING,
    @SerialName("approved") APPROVED,
    @SerialName("rejected") REJECTED
}

@Serializable
data class RequestDto(
    val fromUid: String,
    val status: RequestStatus,
    val createdAt: Long,
    val reviewedAt: Long?
)
