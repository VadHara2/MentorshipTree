@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.vadhara7.mentorship_tree.domain.usecase

import com.vadhara7.mentorship_tree.domain.model.ui.RequestUi
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GetRequestsUseCase(
    private val relationsRepository: RelationsRepository,
    private val firestore: FirebaseFirestore
) {
    operator fun invoke(): Flow<List<RequestUi>> {
        return relationsRepository.getAllRequests()
            .flatMapLatest { requests ->
                val uids = requests.map { it.fromUid }.toSet()
                firestore.usersByIds(uids).map { users ->
                    requests.mapNotNull { dto ->
                        val user = users[dto.fromUid] ?: return@mapNotNull null
                        RequestUi(
                            fromUser = user,
                            status = dto.status,
                            createdAt = dto.createdAt,
                            reviewedAt = dto.reviewedAt,
                            message = dto.message
                        )
                    }
                }
            }
    }

}