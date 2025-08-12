package com.vadhara7.mentorship_tree.domain.usecase

import com.vadhara7.mentorship_tree.data.repository.RelationsRepositoryImpl.Companion.COLLECTION_USERS
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import dev.gitlive.firebase.firestore.FieldPath
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


fun FirebaseFirestore.usersByIds(uids: Set<String>): Flow<Map<String, UserDto>> {
    if (uids.isEmpty()) return flowOf(emptyMap())
    val chunks = uids.chunked(10)
    val flows: List<Flow<List<UserDto>>> = chunks.map { ids ->
        this.collection(COLLECTION_USERS)
            .where { FieldPath.documentId inArray ids }
            .snapshots()
            .map { snap -> snap.documents.map { it.data(UserDto.serializer()) } }
    }
    return combine(flows) { lists ->
        lists.flatMap { it }.associateBy { it.uid }
    }
}
