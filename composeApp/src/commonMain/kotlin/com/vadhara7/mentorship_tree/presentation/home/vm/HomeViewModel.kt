package com.vadhara7.mentorship_tree.presentation.home.vm

import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetOrCreateUserUseCase
import dev.gitlive.firebase.auth.FirebaseAuth
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
    private val auth: FirebaseAuth,
    private val relationsRepository: RelationsRepository
) :
    Processor<HomeIntent, HomeEffect, HomeState> {
    override fun process(intent: HomeIntent, state: HomeState): Flow<HomeEffect> {
        return when (intent) {
            is HomeIntent.Init -> channelFlow {
                launch {
                    getOrCreateUserUseCase().collect {
                        send(HomeEffect.OnUserUpdate(it))
                    }
                }

                launch {
                    val userId = auth.currentUser?.uid ?: return@launch
                    relationsRepository.getTree(
                        userUid = userId,
                        maxMenteeDepth = 3,
                        maxMentorDepth = 3
                    ).collect { tree ->
                        send(HomeEffect.OnMentorshipTreeUpdate(tree))
                    }
                }
            }

            is HomeIntent.OnSignOutClick -> flow {
                auth.signOut()
            }

            is HomeIntent.OnApproveRequest -> flow {
                relationsRepository.approveRequest(intent.request.menteeId)
            }

            is HomeIntent.OnSendRequestClick -> flow {
                relationsRepository.sendRequestToBecomeMentee(state.mentorEmail)
            }

            is HomeIntent.OnMentorEmailChange -> flow {
                emit(HomeEffect.OnMentorEmailChange(intent.email))
            }

            is HomeIntent.OnRejectRequest -> flow {
                relationsRepository.rejectRequest(intent.request.menteeId)
            }
        }
    }
}

class HomeReducer : Reducer<HomeEffect, HomeState> {
    override fun reduce(effect: HomeEffect, state: HomeState): HomeState? {
        return when (effect) {
            is HomeEffect.OnUserUpdate -> state.copy(userName = effect.user.displayName ?: "")
            is HomeEffect.OnRequestUpdate -> state.copy(requests = effect.requests)
            is HomeEffect.OnMentorEmailChange -> state.copy(mentorEmail = effect.email)
            is HomeEffect.OnMentorshipTreeUpdate -> state.copy(mentorshipTree = effect.mentorshipTree)
        }
    }
}