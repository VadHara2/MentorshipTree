package com.vadhara7.mentorship_tree.presentation.home.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetMyTreeUseCase
import com.vadhara7.mentorship_tree.domain.usecase.GetOrCreateUserUseCase
import com.vadhara7.mentorship_tree.presentation.home.vm.TreeEffect.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TreeViewModel(
    processor: TreeProcessor,
    reducer: TreeReducer
) : MviViewModel<TreeIntent, TreeEffect, TreeEvent, TreeState>(
    defaultState = TreeState(),
    processor = processor,
    reducer = reducer
) {
    init {
        process(TreeIntent.Init)
    }
}

class TreeProcessor(
    private val getOrCreateUserUseCase: GetOrCreateUserUseCase,
    private val relationsRepository: RelationsRepository,
    private val getMyTree: GetMyTreeUseCase
) :
    Processor<TreeIntent, TreeEffect, TreeState> {
    override fun process(intent: TreeIntent, state: TreeState): Flow<TreeEffect> {
        return when (intent) {
            is TreeIntent.Init -> channelFlow {
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

            is TreeIntent.OnAddMentorClick -> flow {
                // handle in NavGraph
            }
        }
    }
}

class TreeReducer : Reducer<TreeEffect, TreeState> {
    override fun reduce(effect: TreeEffect, state: TreeState): TreeState? {
        return when (effect) {
            is OnUserUpdate -> state.copy(userName = effect.user.displayName ?: "")
            is OnMentorshipTreeUpdate -> state.copy(mentorshipTree = effect.mentorshipTree)
        }
    }
}