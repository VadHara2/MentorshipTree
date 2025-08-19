package com.vadhara7.mentorship_tree.presentation.tree.vm

import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.MviViewModel
import com.vadhara7.mentorship_tree.core.mvi.Processor
import com.vadhara7.mentorship_tree.core.mvi.Publisher
import com.vadhara7.mentorship_tree.core.mvi.Reducer
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
import com.vadhara7.mentorship_tree.domain.model.dto.UserDto
import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode
import com.vadhara7.mentorship_tree.domain.repository.RelationsRepository
import com.vadhara7.mentorship_tree.domain.usecase.GetMyTreeUseCase
import com.vadhara7.mentorship_tree.domain.usecase.GetOrCreateUserUseCase
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeEffect.*
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeEvent.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TreeViewModel(
    processor: TreeProcessor,
    reducer: TreeReducer,
    publisher: TreePublisher
) : MviViewModel<TreeIntent, TreeEffect, TreeEvent, TreeState>(
    defaultState = TreeState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher
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

            is TreeIntent.OnDeleteRelation -> flow {
                Logger.i("TreeIntent.OnDeleteRelation")
                val response = relationsRepository.deleteRelation(intent.relation)
                emit(
                    OnRelationDeletedResponse(
                        isSuccess = response.isSuccess,
                        relation = intent.relation
                    )
                )
            }

            is TreeIntent.OnRestoreRelation -> flow {
                val response = relationsRepository.restoreRelation(intent.relation)
                emit(
                    OnRelationRestoreResponse(
                        isSuccess = response.isSuccess,
                        relation = intent.relation
                    )
                )
            }

            is TreeIntent.OnSendRestoreRequest -> flow {
                val response = relationsRepository.sendRequestToBecomeMentee(
                    mentorEmail = intent.relation.user.email,
                    message = null
                )

            }
        }
    }
}

class TreeReducer : Reducer<TreeEffect, TreeState> {
    override fun reduce(effect: TreeEffect, state: TreeState): TreeState? {
        return when (effect) {
            is OnUserUpdate -> state.copy(userName = effect.user.displayName ?: "")
            is OnMentorshipTreeUpdate -> state.copy(mentorshipTree = effect.mentorshipTree)
            is OnRelationDeletedResponse -> state
            is OnRelationRestoreResponse -> state
        }
    }
}

class TreePublisher : Publisher<TreeEffect, TreeEvent> {
    override fun publish(effect: TreeEffect): TreeEvent? {
        return when (effect) {
            is OnRelationDeletedResponse -> if (effect.isSuccess) {
                Logger.i("OnRelationDeletedResponse isSuccess")

                ShowSuccessDeleteSnackbarWithCancelAction(effect.relation)
            } else {
                Logger.i("OnRelationDeletedResponse notSuccess")

                ShowFailDeleteSnackbar(effect.relation)
            }

            is OnMentorshipTreeUpdate -> null
            is OnUserUpdate -> null
            is OnRelationRestoreResponse -> if (!effect.isSuccess) {
                ShowFailRestoreSnackbar(effect.relation)
            } else null


        }
    }
}