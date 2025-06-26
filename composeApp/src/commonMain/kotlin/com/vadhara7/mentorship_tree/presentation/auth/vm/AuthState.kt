package com.vadhara7.mentorship_tree.presentation.auth.vm

import com.vadhara7.mentorship_tree.core.mvi.Effect
import com.vadhara7.mentorship_tree.core.mvi.Event
import com.vadhara7.mentorship_tree.core.mvi.Intent
import com.vadhara7.mentorship_tree.core.mvi.State
import dev.gitlive.firebase.auth.FirebaseUser

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthReady: Boolean = false,
    val serverId: String? = null
) : State

sealed interface AuthIntent : Intent {
    data object Init : AuthIntent
    data class OnGoogleAuthProvided(val isProvided: Boolean) : AuthIntent
    data class OnGoogleSignInResult(val result: Result<FirebaseUser?>) : AuthIntent
    data object OnGoogleSignInClick: AuthIntent
}

sealed interface AuthEffect : Effect {
    data class OnServerIdLoaded(val serverId: String) : AuthEffect
    data class UpdateIsAuthReady(val isAuthReady: Boolean) : AuthEffect
    data class UpdateIsLoading(val isLoading: Boolean) : AuthEffect
    data class OnUserSignedIn(val user: FirebaseUser) : AuthEffect
}

sealed interface AuthEvent : Event {
    data object NavigateToHomeScreen : AuthEvent
}