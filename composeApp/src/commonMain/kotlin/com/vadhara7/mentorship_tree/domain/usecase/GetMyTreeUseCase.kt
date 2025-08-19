@file:OptIn(ExperimentalCoroutinesApi::class)

package com.vadhara7.mentorship_tree.domain.usecase

import com.vadhara7.mentorship_tree.domain.model.ui.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
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

class GetMyTreeUseCase(
    private val relationsRepository: RelationsRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    operator fun invoke(
        maxMentorDepth: Int,
        maxMenteeDepth: Int
    ): Flow<MentorshipTree> {
        val myUid = auth.currentUser?.uid ?: error("User not authenticated")

        val mentorsUidFlow = buildUidTree(myUid, RelationType.MENTOR, maxMentorDepth)
        val menteesUidFlow = buildUidTree(myUid, RelationType.MENTEE, maxMenteeDepth)

        return combine(mentorsUidFlow, menteesUidFlow) { mentorsUid, menteesUid ->
            mentorsUid to menteesUid
        }.flatMapLatest { (mentorsUid, menteesUid) ->
            val uids = collectUids(mentorsUid) + collectUids(menteesUid) + setOf(myUid)
            firestore.usersByIds(uids).map { usersMap ->
                MentorshipTree(
                    mentors = mapNodes(mentorsUid, usersMap),
                    mentees = mapNodes(menteesUid, usersMap)
                )
            }
        }
    }


    private data class UidNode(
        val userUid: String,
        val type: RelationType,
        val since: Long,
        val children: List<UidNode> = emptyList()
    )

    private fun buildUidTree(
        userUid: String,
        direction: RelationType,
        depth: Int
    ): Flow<List<UidNode>> =
        relationsRepository.getByGeneration(userUid, direction, 1).flatMapLatest { firstGen ->
            if (depth <= 1) {
                flowOf(firstGen.map { dto ->
                    UidNode(dto.userUid, dto.type, dto.since, emptyList())
                })
            } else {
                val nodeFlows = firstGen.map { dto ->
                    buildUidTree(dto.userUid, direction, depth - 1).map { children ->
                        UidNode(dto.userUid, dto.type, dto.since, children)
                    }
                }
                if (nodeFlows.isEmpty()) flowOf(emptyList())
                else combine(*nodeFlows.toTypedArray()) { it.toList() }
            }
        }

    private fun collectUids(nodes: List<UidNode>): Set<String> {
        val set = mutableSetOf<String>()
        fun dfs(n: UidNode) {
            set += n.userUid
            n.children.forEach(::dfs)
        }
        nodes.forEach(::dfs)
        return set
    }

    private fun mapNodes(
        nodes: List<UidNode>,
        users: Map<String, UserDto>
    ): List<RelationNode> = nodes.mapNotNull { n ->
        val u = users[n.userUid] ?: return@mapNotNull null
        RelationNode(
            user = u,
            type = n.type,
            since = n.since,
            children = mapNodes(n.children, users)
        )
    }

}