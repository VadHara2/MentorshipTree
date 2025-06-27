package com.vadhara7.mentorship_tree.presentation.home.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.usecase.GetOrCreateUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeViewModel(
    processor: HomeProcessor,
    reducer: HomeReducer
) : MviViewModel<HomeIntent, HomeEffect, HomeEvent, HomeState>(
    defaultState = HomeState(),
    processor = processor,
    reducer = reducer
) {
    init {
        process(HomeIntent.Init)
    }
}

class HomeProcessor(private val getOrCreateUserUseCase: GetOrCreateUserUseCase) :
    Processor<HomeIntent, HomeEffect, HomeState> {
    override fun process(intent: HomeIntent, state: HomeState): Flow<HomeEffect> {
        return when (intent) {
            is HomeIntent.Init -> flow {

                getOrCreateUserUseCase().collect {
                    emit(HomeEffect.OnUserUpdate(it))
                }
            }

        }
    }
}

class HomeReducer : Reducer<HomeEffect, HomeState> {
    override fun reduce(effect: HomeEffect, state: HomeState): HomeState? {
        return when (effect) {
            is HomeEffect.OnUserUpdate -> state.copy(userName = effect.user.displayName ?: "")
        }
    }
}