package com.vadhara7.mentorship_tree.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Simple MVI style view model for authentication. */
class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenProvider: AuthTokenProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            AuthIntent.SignOut -> signOut()
            AuthIntent.SignInWithGoogle -> signInWithGoogle()
            AuthIntent.SignInWithApple -> signInWithApple()
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val token = tokenProvider.getGoogleIdToken()
            if (token == null) {
                _state.value = AuthState.Error("Token not found")
                return@launch
            }
            runCatching { repository.signInWithGoogle(token) }
                .onSuccess { _state.value = AuthState.Success(it) }
                .onFailure { _state.value = AuthState.Error(it.message.orEmpty()) }
        }
    }

    private fun signInWithApple() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val token = tokenProvider.getAppleIdToken()
            if (token == null) {
                _state.value = AuthState.Error("Token not found")
                return@launch
            }
            runCatching { repository.signInWithApple(token) }
                .onSuccess { _state.value = AuthState.Success(it) }
                .onFailure { _state.value = AuthState.Error(it.message.orEmpty()) }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            runCatching { repository.signOut() }
            _state.value = AuthState.Idle
        }
    }
}
