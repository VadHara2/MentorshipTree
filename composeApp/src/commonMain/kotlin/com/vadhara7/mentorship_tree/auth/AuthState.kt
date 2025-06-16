package com.vadhara7.mentorship_tree.auth

/** State of the authentication flow. */
sealed interface AuthState {
    /** No operation in progress. */
    data object Idle : AuthState

    /** Authentication request is in progress. */
    data object Loading : AuthState

    /** Successfully authenticated. */
    data class Success(val userId: String) : AuthState

    /** Error during authentication. */
    data class Error(val message: String) : AuthState
}
