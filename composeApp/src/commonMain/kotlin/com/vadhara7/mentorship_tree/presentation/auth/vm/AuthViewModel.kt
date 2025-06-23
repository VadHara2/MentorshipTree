package com.vadhara7.mentorship_tree.presentation.auth.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
//import com.vadhara7.mentorship_tree.domain.repository.SecretsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
//import org.koin.android.annotation.KoinViewModel
//import org.koin.core.annotation.Factory

//@KoinViewModel
class AuthViewModel(
    processor: AuthProcessor,
    reducer: AuthReducer
) : MviViewModel<AuthIntent, AuthEffect, AuthEvent, AuthState>(
    defaultState = AuthState(),
    processor = processor,
    reducer = reducer
) {
    init {
        process(AuthIntent.Init)
    }
}

//@Factory
class AuthProcessor(
//    private val secretsRepository: SecretsRepository
) : Processor<AuthIntent, AuthEffect, AuthState> {
    override fun process(intent: AuthIntent, state: AuthState): Flow<AuthEffect> {
        return when (intent) {
            is AuthIntent.Init -> flow {
                emit(AuthEffect.UpdateIsLoading(true))
//                val serverId = secretsRepository.getGoogleAuthServerId()
//                emit(AuthEffect.OnServerIdLoaded(serverId))
            }

            is AuthIntent.OnGoogleAuthProvided -> flow {
                emit(AuthEffect.UpdateIsAuthReady(true))
                emit(AuthEffect.UpdateIsLoading(false))
            }

            is AuthIntent.OnGoogleSignInResult -> flow {
                Logger.i("Google Sign In Result: ${intent.result}")
            }
        }
    }
}

//@Factory
class AuthReducer : Reducer<AuthEffect, AuthState> {
    override fun reduce(effect: AuthEffect, state: AuthState): AuthState {
        return when (effect) {
            is AuthEffect.OnServerIdLoaded -> state.copy(serverId = effect.serverId)
            is AuthEffect.UpdateIsAuthReady -> state.copy(isAuthReady = effect.isAuthReady)
            is AuthEffect.UpdateIsLoading -> state.copy(isLoading = effect.isLoading)
        }
    }
}