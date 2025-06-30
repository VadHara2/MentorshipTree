package com.vadhara7.mentorship_tree.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class RequestStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("APPROVED") APPROVED,
    @SerialName("REJECTED") REJECTED
}

@Serializable
data class RequestDto(
    val fromUid: String,
    val status: RequestStatus,
    val createdAt: Long,
    val reviewedAt: Long?
)
