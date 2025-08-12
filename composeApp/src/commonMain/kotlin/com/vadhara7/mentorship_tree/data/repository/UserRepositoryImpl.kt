@file:OptIn(ExperimentalTime::class)

package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.repository.UserRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UserRepositoryImpl(private val firestore: FirebaseFirestore) : UserRepository {
    private companion object {
        const val USERS_COLLECTION = "users"
    }

    override fun getUser(uid: String): Flow<UserDto> {

        return firestore.collection(USERS_COLLECTION)
            .document(uid)
            .snapshots
            .map {
                if (it.exists) {
                    it.data(UserDto.serializer())
                } else {
                    throw NoSuchElementException("User with uid=$uid not found")
                }
            }
    }

    override suspend fun createUser(currentUser: FirebaseUser): Result<Boolean> {

        return runCatching {

            val userDto = UserDto(
                uid = currentUser.uid,
                displayName = currentUser.displayName,
                email = currentUser.email ?: "",
                createdAt = Clock.System.now().epochSeconds
            )
            firestore.collection(USERS_COLLECTION)
                .document(userDto.uid)
                .set(strategy = UserDto.serializer(), data = userDto)
            true
        }
    }

    override suspend fun updateUser(user: UserDto): Result<Boolean> {
        return runCatching {
            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(strategy = UserDto.serializer(), data = user, merge = true)
            true
        }
    }


}