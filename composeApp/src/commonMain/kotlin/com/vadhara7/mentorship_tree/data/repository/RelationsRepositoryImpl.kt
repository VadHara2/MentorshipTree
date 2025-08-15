@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.model.dto.RelationDto
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
import com.vadhara7.mentorship_tree.domain.model.dto.RequestDto
import com.vadhara7.mentorship_tree.domain.model.dto.RequestStatus
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class RelationsRepositoryImpl(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) : RelationsRepository {
    private val myUid: String
        get() = auth.currentUser?.uid ?: error("User not authenticated")


    companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_RELATIONS = "relations"
        const val COLLECTION_REQUESTS = "requests"
    }


    override fun getByGeneration(
        userUid: String,
        direction: RelationType,
        generation: Int
    ): Flow<List<RelationDto>> {

        return if (generation == 1) {
            firestore.collection(COLLECTION_USERS)
                .document(userUid)
                .collection(COLLECTION_RELATIONS)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents
                        .map { it.data(RelationDto.serializer()) }
                        .filter { it.type == direction }
                }
        } else {
            // For deeper generations, fetch the first generation and then recursively fetch further
            getByGeneration(userUid, direction, 1).flatMapLatest { firstGen ->
                val flows = firstGen.map { relation ->
                    getByGeneration(relation.userUid, direction, generation - 1)
                }
                if (flows.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(*flows.toTypedArray()) { results ->
                        results.flatMap { it }
                    }
                }
            }
        }
    }

    override fun getAllRequests(): Flow<List<RequestDto>> {
        return firestore.collection(COLLECTION_USERS)
            .document(myUid)
            .collection(COLLECTION_REQUESTS)
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(RequestDto.serializer()) }
            }
    }

    override suspend fun sendRequestToBecomeMentee(
        mentorEmail: String,
        message: String?
    ): Result<Unit> {
        return runCatching {
            val request = RequestDto(
                fromUid = myUid,
                status = RequestStatus.PENDING,
                createdAt = Clock.System.now().epochSeconds,
                reviewedAt = null,
                message = message
            )
            val querySnapshot = firestore.collection(COLLECTION_USERS)
                .where { "email" equalTo mentorEmail }
                .get()

            val mentorDto = querySnapshot.documents
                .firstOrNull()
                ?.data<UserDto>()
                ?: return Result.failure(Exception("Mentor with email $mentorEmail not found"))

            firestore.collection(COLLECTION_USERS)
                .document(mentorDto.uid)
                .collection(COLLECTION_REQUESTS)
                .document(myUid)
                .set(RequestDto.serializer(), request)
        }
    }

    override suspend fun approveRequest(
        menteeUid: String
    ): Result<Unit> {
        return runCatching {
            val batch = firestore.batch()
            val requestRef = firestore.collection(COLLECTION_USERS)
                .document(myUid)
                .collection(COLLECTION_REQUESTS)
                .document(menteeUid)
            // Build an updated RequestDto with approved status, but keep original createdAt if present
            val existingRequest = try {
                requestRef.get().data(RequestDto.serializer())
            } catch (_: Exception) {
                null
            }
            val updatedApprovedRequest = RequestDto(
                fromUid = menteeUid,
                status = RequestStatus.APPROVED,
                createdAt = existingRequest?.createdAt ?: Clock.System.now().epochSeconds, // preserve if exists
                reviewedAt = Clock.System.now().epochSeconds,
                message = existingRequest?.message
            )
            batch.set(requestRef, RequestDto.serializer(), updatedApprovedRequest, merge = true)
            // Create confirmed relation for teacher -> student
            val teacherRelRef = firestore.collection(COLLECTION_USERS)
                .document(myUid)
                .collection(COLLECTION_RELATIONS)
                .document(menteeUid)
            batch.set(
                teacherRelRef,
                RelationDto.serializer(),
                RelationDto(menteeUid, RelationType.MENTEE, Clock.System.now().epochSeconds)
            )
            // Create confirmed relation for student -> teacher
            val studentRelRef = firestore.collection(COLLECTION_USERS)
                .document(menteeUid)
                .collection(COLLECTION_RELATIONS)
                .document(myUid)
            batch.set(
                studentRelRef,
                RelationDto.serializer(),
                RelationDto(myUid, RelationType.MENTOR, Clock.System.now().epochSeconds)
            )
            batch.commit()
        }
    }

    override suspend fun rejectRequest(
        menteeUid: String
    ): Result<Unit> {
        return runCatching {
            // Build an updated RequestDto with rejected status, but keep original createdAt if present
            val requestRef = firestore.collection(COLLECTION_USERS)
                .document(myUid)
                .collection(COLLECTION_REQUESTS)
                .document(menteeUid)
            val existingRequest = try {
                requestRef.get().data(RequestDto.serializer())
            } catch (_: Exception) {
                null
            }
            val updatedRejectedRequest = RequestDto(
                fromUid = menteeUid,
                status = RequestStatus.REJECTED,
                createdAt = existingRequest?.createdAt ?: Clock.System.now().epochSeconds, // preserve if exists
                reviewedAt = Clock.System.now().epochSeconds,
                message = existingRequest?.message
            )
            requestRef.set(RequestDto.serializer(), updatedRejectedRequest, merge = true)
        }
    }

    override suspend fun deleteRelation(relation: RelationDto): Result<Unit> {
        TODO("Not yet implemented")
    }
}