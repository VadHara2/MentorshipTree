package com.vadhara7.mentorship_tree.auth

/** Platform-specific provider of authentication tokens. */
interface AuthTokenProvider {
    suspend fun getGoogleIdToken(): String?
    suspend fun getAppleIdToken(): String?
}
