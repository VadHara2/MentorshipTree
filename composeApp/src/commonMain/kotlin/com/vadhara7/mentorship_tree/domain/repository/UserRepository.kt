package com.vadhara7.mentorship_tree.domain.repository

import com.vadhara7.mentorship_tree.domain.model.UserDto
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(uid: String): Flow<UserDto>

    suspend fun createUser(currentUser: FirebaseUser): Result<Boolean>

    suspend fun updateUser(user: UserDto): Result<Boolean>
}