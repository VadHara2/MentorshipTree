package com.vadhara7.mentorship_tree.domain.usecase

import com.vadhara7.mentorship_tree.domain.model.UserDto
import com.vadhara7.mentorship_tree.domain.repository.UserRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll

class GetOrCreateUserUseCase(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {

    /**
     * Повертає Flow<UserDto>.
     * Якщо документ не знайдено — створює його й знову підписується.
     */
    operator fun invoke(): Flow<UserDto> {
        val currentUser = auth.currentUser ?: error("User is not authenticated")

        return userRepository.getUser(currentUser.uid)
            .catch { e ->
                if (e is NoSuchElementException) {
                    userRepository.createUser(currentUser).getOrThrow()

                    emitAll(userRepository.getUser(currentUser.uid))
                } else {
                    throw e
                }
            }
    }
}