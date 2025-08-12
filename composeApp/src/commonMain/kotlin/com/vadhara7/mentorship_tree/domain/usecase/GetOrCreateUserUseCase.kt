@file:OptIn(ExperimentalCoroutinesApi::class)

package com.vadhara7.mentorship_tree.domain.usecase

import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.repository.UserRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.emptyFlow

class GetOrCreateUserUseCase(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {

    /**
     * Повертає Flow<UserDto>.
     * Якщо документ не знайдено — створює його й знову підписується.
     */
    operator fun invoke(): Flow<UserDto> {
        return auth.authStateChanged
            .flatMapLatest { firebaseUser ->
                if (firebaseUser != null) {
                    userRepository.getUser(firebaseUser.uid)
                        .catch { e ->
                            if (e is NoSuchElementException) {
                                userRepository.createUser(firebaseUser).getOrThrow()
                                emitAll(userRepository.getUser(firebaseUser.uid))
                            } else {
                                throw e
                            }
                        }
                } else {
                    emptyFlow()
                }
            }
    }
}