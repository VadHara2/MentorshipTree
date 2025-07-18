package com.vadhara7.mentorship_tree.presentation.auth.vm

import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Publisher
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.repository.SecretsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthViewModel(
    processor: AuthProcessor,
    reducer: AuthReducer,
    publisher: AuthPublisher
) : MviViewModel<AuthIntent, AuthEffect, AuthEvent, AuthState>(
    defaultState = AuthState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher,
) {
    init {
        process(AuthIntent.Init)
    }
}


class AuthProcessor(
    private val secretsRepository: SecretsRepository
) : Processor<AuthIntent, AuthEffect, AuthState> {
    override fun process(intent: AuthIntent, state: AuthState): Flow<AuthEffect> {
        return when (intent) {
            is AuthIntent.Init -> flow {
                emit(AuthEffect.UpdateIsLoading(true))
                val serverId = secretsRepository.getGoogleAuthServerId()
                emit(AuthEffect.OnServerIdLoaded(serverId))
            }

            is AuthIntent.OnGoogleAuthProvided -> flow {
                emit(AuthEffect.UpdateIsAuthReady(intent.isProvided))
                emit(AuthEffect.UpdateIsLoading(false))
            }

            is AuthIntent.OnGoogleSignInResult -> flow {
                if (intent.result.isSuccess) {
                    intent.result.getOrNull()?.let { emit(AuthEffect.OnUserSignedIn(it)) }
                        ?: error("User is null")
                }

                emit(AuthEffect.UpdateIsLoading(false))
            }

            is AuthIntent.OnGoogleSignInClick -> flow {
                emit(AuthEffect.UpdateIsLoading(true))
                // Вся логіка у GoogleButtonUiContainerFirebase
            }
        }
    }
}


class AuthReducer : Reducer<AuthEffect, AuthState> {
    override fun reduce(effect: AuthEffect, state: AuthState): AuthState {
        return when (effect) {
            is AuthEffect.OnServerIdLoaded -> state.copy(serverId = effect.serverId)
            is AuthEffect.UpdateIsAuthReady -> state.copy(isAuthReady = effect.isAuthReady)
            is AuthEffect.UpdateIsLoading -> state.copy(isLoading = effect.isLoading)
            is AuthEffect.OnUserSignedIn -> state
        }
    }
}

class AuthPublisher : Publisher<AuthEffect, AuthEvent> {
    override fun publish(effect: AuthEffect): AuthEvent? {
        return when (effect) {
            is AuthEffect.OnServerIdLoaded -> null
            is AuthEffect.UpdateIsAuthReady -> null
            is AuthEffect.UpdateIsLoading -> null
            is AuthEffect.OnUserSignedIn -> AuthEvent.NavigateToHomeScreen
        }
    }

}