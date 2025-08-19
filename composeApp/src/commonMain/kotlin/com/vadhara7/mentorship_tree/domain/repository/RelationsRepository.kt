package com.vadhara7.mentorship_tree.domain.repository

import com.vadhara7.mentorship_tree.domain.model.dto.RelationDto
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
import com.vadhara7.mentorship_tree.domain.model.dto.RequestDto
import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode
import kotlinx.coroutines.flow.Flow


/**
 * Репозиторій для mentorship-зв’язків з можливістю вказати покоління.
 */
interface RelationsRepository {

    /**
     * Повертає потік зв’язків заданого напрямку і глибини.
     *
     * @param userUid    для кого дивимось (Auth UID)
     * @param direction  MENTOR або MENTEE
     * @param generation номер покоління (1 – прямі; 2 – діти дітей; …)
     */
    fun getByGeneration(
        userUid: String,
        direction: RelationType,
        generation: Int = 1
    ): Flow<List<RelationDto>>

    /**
     * Потік “очікуючих” запитів, які студент надіслав до вчителів.
     */
    fun getAllRequests(): Flow<List<RequestDto>>

    /**
     * Надіслати запит від studentUid до teacherUid.
     */
    suspend fun sendRequestToBecomeMentee(mentorEmail: String, message: String?): Result<Unit>

    /**
     * Затвердити pending-запит — перенести в relations.
     */
    suspend fun approveRequest(menteeUid: String): Result<Unit>

    /**
     * Відхилити pending-запит.
     */
    suspend fun rejectRequest(menteeUid: String): Result<Unit>

    /**
     * Повернути запит до стану pending після затвердження чи відхилення.
     */
    suspend fun restoreRequestToPending(menteeUid: String): Result<Unit>

    /**
     * Видалити зв’язок.
     */
    suspend fun deleteRelation(relation: RelationNode): Result<Unit>

    /**
     * Відновити зв’язок.
     */
    suspend fun restoreRelation(relation: RelationNode): Result<Unit>
}