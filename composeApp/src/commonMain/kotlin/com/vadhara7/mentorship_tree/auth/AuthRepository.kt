package com.vadhara7.mentorship_tree.auth

/**
 * Abstraction over authentication actions.
 */
interface AuthRepository {
    /** Returns the id of the currently signed-in user or `null` if there is none. */
    fun currentUserId(): String?

    /** Sign in with a Google ID token and return user id. */
    suspend fun signInWithGoogle(idToken: String): String

    /** Sign in with an Apple ID token and return user id. */
    suspend fun signInWithApple(idToken: String): String

    /** Signs out the current user. */
    suspend fun signOut()
}
