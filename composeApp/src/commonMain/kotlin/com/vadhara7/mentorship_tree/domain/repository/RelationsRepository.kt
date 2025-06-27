package com.vadhara7.mentorship_tree.domain.repository

import com.vadhara7.mentorship_tree.domain.model.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.RelationDto
import com.vadhara7.mentorship_tree.domain.model.RelationType
import com.vadhara7.mentorship_tree.domain.model.RequestDto
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
     * Зібрати всі покоління до заданої глибини в одну структуру.
     *
     * @param userUid     для кого дивимось
     * @param direction   напрямок (Mentor або Mentee)
     * @param maxDepth    глибина (наприклад, 3 – до третього покоління включно)
     * @return Map<покоління, список Relation>
     */
    fun getTree(
        userUid: String,
        maxMentorDepth: Int,
        maxMenteeDepth: Int
    ): Flow<MentorshipTree>

    /**
     * Потік “очікуючих” запитів, які студент надіслав до вчителів.
     */
    fun getPendingRequests(teacherUid: String): Flow<List<RequestDto>>

    /**
     * Надіслати запит від studentUid до teacherUid.
     */
    suspend fun sendRequest(teacherUid: String, studentUid: String): Result<Unit>

    /**
     * Затвердити pending-запит — перенести в relations.
     */
    suspend fun approveRequest(teacherUid: String, studentUid: String): Result<Unit>

    /**
     * Відхилити pending-запит.
     */
    suspend fun rejectRequest(teacherUid: String, studentUid: String): Result<Unit>
}