@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)

package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.model.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.RelationDto
import com.vadhara7.mentorship_tree.domain.model.RelationNode
import com.vadhara7.mentorship_tree.domain.model.RelationType
import com.vadhara7.mentorship_tree.domain.model.RequestDto
import com.vadhara7.mentorship_tree.domain.model.RequestStatus
import com.vadhara7.mentorship_tree.domain.model.UserDto
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

    override fun getTree(
        userUid: String,
        maxMentorDepth: Int,
        maxMenteeDepth: Int
    ): Flow<MentorshipTree> {
        // Build mentor and mentee subtrees in parallel
        val mentorsFlow = buildTree(userUid, RelationType.MENTOR, maxMentorDepth)
        val menteesFlow = buildTree(userUid, RelationType.MENTEE, maxMenteeDepth)
        // Combine into a single MentorshipTree object
        return combine(mentorsFlow, menteesFlow) { mentors, mentees ->
            MentorshipTree(
                mentors = mentors,
                mentees = mentees
            )
        }
    }

    override fun getPendingRequests(teacherUid: String): Flow<List<RequestDto>> {
        return firestore.collection(COLLECTION_USERS)
            .document(teacherUid)
            .collection(COLLECTION_REQUESTS)
            .snapshots()
            .map { snapshot ->
                snapshot.documents
                    .map { it.data(RequestDto.serializer()) }
                    .filter { it.status == RequestStatus.PENDING }
            }
    }

    override suspend fun sendRequestToBecomeMentee(
        mentorEmail: String
    ): Result<Unit> {
        return runCatching {
            val request = RequestDto(
                fromUid = myUid,
                status = RequestStatus.PENDING,
                createdAt = Clock.System.now().epochSeconds,
                reviewedAt = null
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
        teacherUid: String,
        studentUid: String
    ): Result<Unit> {
        return runCatching {
            val batch = firestore.batch()
            val requestRef = firestore.collection(COLLECTION_USERS)
                .document(teacherUid)
                .collection(COLLECTION_REQUESTS)
                .document(studentUid)
            // Build an updated RequestDto with approved status
            val updatedApprovedRequest = RequestDto(
                fromUid = studentUid,
                status = RequestStatus.APPROVED,
                createdAt = Clock.System.now().epochSeconds,         // or retain original creation time if available
                reviewedAt = Clock.System.now().epochSeconds
            )
            batch.set(requestRef, RequestDto.serializer(), updatedApprovedRequest, merge = true)
            // Create confirmed relation for teacher -> student
            val teacherRelRef = firestore.collection(COLLECTION_USERS)
                .document(teacherUid)
                .collection(COLLECTION_RELATIONS)
                .document(studentUid)
            batch.set(
                teacherRelRef,
                RelationDto.serializer(),
                RelationDto(studentUid, RelationType.MENTEE, Clock.System.now().epochSeconds)
            )
            // Create confirmed relation for student -> teacher
            val studentRelRef = firestore.collection(COLLECTION_USERS)
                .document(studentUid)
                .collection(COLLECTION_RELATIONS)
                .document(teacherUid)
            batch.set(
                studentRelRef,
                RelationDto.serializer(),
                RelationDto(teacherUid, RelationType.MENTOR, Clock.System.now().epochSeconds)
            )
            batch.commit()
        }
    }

    override suspend fun rejectRequest(
        teacherUid: String,
        studentUid: String
    ): Result<Unit> {
        return runCatching {
            // Build an updated RequestDto with rejected status
            val updatedRejectedRequest = RequestDto(
                fromUid = studentUid,
                status = RequestStatus.REJECTED,
                createdAt = Clock.System.now().epochSeconds,
                reviewedAt = Clock.System.now().epochSeconds
            )
            firestore.collection(COLLECTION_USERS)
                .document(teacherUid)
                .collection(COLLECTION_REQUESTS)
                .document(studentUid)
                .set(RequestDto.serializer(), updatedRejectedRequest, merge = true)
        }
    }

    private fun buildTree(
        userUid: String,
        direction: RelationType,
        depth: Int
    ): Flow<List<RelationNode>> = getByGeneration(userUid, direction, 1).flatMapLatest { firstGen ->
        if (depth <= 1) {
            // Перший рівень — просто конвертуємо RelationDto у RelationNode без дітей
            flowOf(firstGen.map { dto ->
                RelationNode(
                    userUid = dto.userUid,
                    type = dto.type,
                    since = dto.since,
                    children = emptyList()
                )
            })
        } else {
            // Для глибини >1 будуємо для кожного вузла свій піддерево
            val nodeFlows = firstGen.map { dto ->
                buildTree(dto.userUid, direction, depth - 1).map { children ->
                    RelationNode(
                        userUid = dto.userUid,
                        type = dto.type,
                        since = dto.since,
                        children = children
                    )
                }
            }
            if (nodeFlows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(*nodeFlows.toTypedArray()) { results ->
                    (results as Array<RelationNode>).toList()
                }
            }
        }
    }
}