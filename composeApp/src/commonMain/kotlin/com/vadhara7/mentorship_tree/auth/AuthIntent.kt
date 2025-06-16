package com.vadhara7.mentorship_tree.auth

/** Events that can be sent to [AuthViewModel]. */
sealed interface AuthIntent {
    data object SignInWithGoogle : AuthIntent
    data object SignInWithApple : AuthIntent
    data object SignOut : AuthIntent
}
