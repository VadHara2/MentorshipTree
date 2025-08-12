package com.vadhara7.mentorship_tree.presentation.home.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetMyTreeUseCase
import com.vadhara7.mentorship_tree.domain.usecase.GetOrCreateUserUseCase
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeEffect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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

class HomeProcessor(
    private val getOrCreateUserUseCase: GetOrCreateUserUseCase,
    private val relationsRepository: RelationsRepository,
    private val getMyTree: GetMyTreeUseCase
) :
    Processor<HomeIntent, HomeEffect, HomeState> {
    override fun process(intent: HomeIntent, state: HomeState): Flow<HomeEffect> {
        return when (intent) {
            is HomeIntent.Init -> channelFlow {
                launch {
                    getOrCreateUserUseCase().collect {
                        send(OnUserUpdate(it))
                    }
                }

                launch {
                    getMyTree(
                        maxMenteeDepth = 3,
                        maxMentorDepth = 3
                    ).collect { tree ->
                        send(OnMentorshipTreeUpdate(tree))
                    }
                }
            }

            is HomeIntent.AddMentorByEmail -> flow {
                emit(UpdateAddMentorDialog(false))
                val result = relationsRepository.sendRequestToBecomeMentee(
                    mentorEmail = intent.email,
                    message = intent.message
                )
                if (result.isSuccess) {
                    Logger.i("HomeIntent.AddMentorByEmail: isSuccess")
                    // todo show success snackbar
                }
                if (result.isFailure) {
                    Logger.e("HomeIntent.AddMentorByEmail: isFailure")
                    // todo show fail snackbar
                }
            }

            is HomeIntent.OnAddMentorClick -> flow {
                emit(UpdateAddMentorDialog(true))
            }

            is HomeIntent.OnCloseDialogClick -> flow {
                emit(UpdateAddMentorDialog(false))
            }
        }
    }
}

class HomeReducer : Reducer<HomeEffect, HomeState> {
    override fun reduce(effect: HomeEffect, state: HomeState): HomeState? {
        return when (effect) {
            is OnUserUpdate -> state.copy(userName = effect.user.displayName ?: "")
            is OnMentorshipTreeUpdate -> state.copy(mentorshipTree = effect.mentorshipTree)
            is UpdateAddMentorDialog -> state.copy(showAddMentorByEmailDialog = effect.isShow)
        }
    }
}