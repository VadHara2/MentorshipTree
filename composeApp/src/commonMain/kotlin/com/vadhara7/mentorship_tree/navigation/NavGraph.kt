package com.vadhara7.mentorship_tree.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.vadhara7.mentorship_tree.core.mvi.ObserveAsEvents
import com.vadhara7.mentorship_tree.domain.model.dto.RelationType
import com.vadhara7.mentorship_tree.presentation.addMentor.ui.AddMentorScreen
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorEvent
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorIntent
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorViewModel
import com.vadhara7.mentorship_tree.presentation.auth.ui.AuthScreen
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthEvent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.tree.ui.TreeScreen
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeIntent
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeViewModel
import com.vadhara7.mentorship_tree.presentation.notification.ui.NotificationScreen
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationViewModel
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationEvent
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationIntent
import com.vadhara7.mentorship_tree.presentation.snackbars.ProvideSnackbarController
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeEvent
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.cancel
import mentorshiptree.composeapp.generated.resources.failed_deletion
import mentorshiptree.composeapp.generated.resources.try_delete_again
import mentorshiptree.composeapp.generated.resources.failed_restoration
import mentorshiptree.composeapp.generated.resources.try_restore_again
import mentorshiptree.composeapp.generated.resources.send_request_to_restore
import mentorshiptree.composeapp.generated.resources.restore_relation
import mentorshiptree.composeapp.generated.resources.success_deletion
import mentorshiptree.composeapp.generated.resources.failed_send_request
import mentorshiptree.composeapp.generated.resources.request_accepted
import mentorshiptree.composeapp.generated.resources.request_accept_failed
import mentorshiptree.composeapp.generated.resources.request_declined
import mentorshiptree.composeapp.generated.resources.request_decline_failed
import mentorshiptree.composeapp.generated.resources.send_request
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


fun NavController.customPopBackStack() {
    popBackStack()
}

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val user by Firebase.auth.authStateChanged
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)
    val navigationPages: List<NavigationPage> = listOf(
        MainRouter.TreeScreen,
        MainRouter.NotificationScreen
    )

    // Routes where the bottom bar must be hidden
    val hiddenRoutes = remember {
        setOf(
            MainRouter.AuthScreen::class.qualifiedName,
            MainRouter.AddMentorScreen::class.qualifiedName
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Recomputes automatically when the nav back stack changes
    val shouldDisplayBottomBar = currentRoute !in hiddenRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = shouldDisplayBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar {
                    navigationPages.forEach {
                        val isSelected = currentRoute == it::class.qualifiedName

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) navController.navigate(it)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(it.iconRes),
                                    contentDescription = stringResource(it.titleRes)
                                )
                            }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ProvideSnackbarController(hostState = snackbarHostState) { snackbarController ->
            NavHost(
                navController = navController,
                startDestination = if (user == null) MainRouter.AuthScreen else MainRouter.TreeScreen,
                modifier = modifier
            ) {

                composable<MainRouter.AuthScreen> {
                    val viewModel = koinViewModel<AuthViewModel>()
                    val state = viewModel.state.collectAsStateWithLifecycle()

                    ObserveAsEvents(viewModel.event) { event ->
                        when (event) {
                            is AuthEvent.NavigateToHomeScreen -> {
                                navController.navigate(MainRouter.TreeScreen)
                            }
                        }
                    }

                    AuthScreen(
                        state = state.value,
                        onIntent = viewModel::process
                    )
                }


                composable<MainRouter.TreeScreen> {
                    val viewModel = koinViewModel<TreeViewModel>()
                    val state = viewModel.state.collectAsStateWithLifecycle()

                    val txtFailedDeletion = stringResource(Res.string.failed_deletion)
                    val txtTryDeleteAgain = stringResource(Res.string.try_delete_again)
                    val txtFailedRestoration = stringResource(Res.string.failed_restoration)
                    val txtTryRestoreAgain = stringResource(Res.string.try_restore_again)
                    val txtSendRequestToRestore = stringResource(Res.string.send_request_to_restore)
                    val txtRestoreRelation = stringResource(Res.string.restore_relation)
                    val txtSuccessDeletion = stringResource(Res.string.success_deletion)

                    ObserveAsEvents(viewModel.event) { event ->
                        when (event) {
                            is TreeEvent.ShowFailDeleteSnackbar -> {

                                snackbarController.showAsync(
                                    message = txtFailedDeletion,
                                    actionLabel = txtTryDeleteAgain,
                                    onAction = {
                                        viewModel.process(TreeIntent.OnDeleteRelation(event.relation))
                                    }
                                )
                            }

                            is TreeEvent.ShowFailRestoreSnackbar -> {

                                snackbarController.showAsync(
                                    message = txtFailedRestoration,
                                    actionLabel = txtTryRestoreAgain,
                                    onAction = {
                                        viewModel.process(TreeIntent.OnRestoreRelation(event.relation))
                                    }
                                )
                            }

                            is TreeEvent.ShowSuccessDeleteSnackbarWithCancelAction -> {

                                val actionLabel = when (event.relation.type) {
                                    RelationType.MENTOR -> txtSendRequestToRestore
                                    RelationType.MENTEE -> txtRestoreRelation
                                }

                                val actionIntent = when (event.relation.type) {
                                    RelationType.MENTOR -> TreeIntent.OnSendRestoreRequest(event.relation)
                                    RelationType.MENTEE -> TreeIntent.OnRestoreRelation(event.relation)
                                }

                                snackbarController.showAsync(
                                    message = txtSuccessDeletion,
                                    actionLabel = actionLabel,
                                    onAction = {
                                        viewModel.process(actionIntent)
                                    }
                                )
                            }
                        }
                    }

                    TreeScreen(
                        state = state.value,
                        onIntent = { intent ->
                            viewModel.process(intent)
                            when (intent) {
                                is TreeIntent.OnAddMentorClick -> navController.navigate(MainRouter.AddMentorScreen)
                                else -> {}
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable<MainRouter.NotificationScreen> {
                    val viewModel = koinViewModel<NotificationViewModel>()
                    val state = viewModel.state.collectAsStateWithLifecycle()

                    val txtRequestAccepted = stringResource(Res.string.request_accepted)
                    val txtRequestAcceptFailed = stringResource(Res.string.request_accept_failed)
                    val txtRequestDeclined = stringResource(Res.string.request_declined)
                    val txtRequestDeclineFailed = stringResource(Res.string.request_decline_failed)
                    val txtCancel = stringResource(Res.string.cancel)

                    ObserveAsEvents(viewModel.event) { event ->
                        when (event) {
                            is NotificationEvent.ShowAcceptSuccess -> snackbarController.showAsync(
                                message = txtRequestAccepted,
                                actionLabel = txtCancel,
                                onAction = { viewModel.process(NotificationIntent.RestoreRequest(event.userId)) }
                            )
                            NotificationEvent.ShowAcceptFailure -> snackbarController.showAsync(message = txtRequestAcceptFailed)
                            is NotificationEvent.ShowDeclineSuccess -> snackbarController.showAsync(
                                message = txtRequestDeclined,
                                actionLabel = txtCancel,
                                onAction = { viewModel.process(NotificationIntent.RestoreRequest(event.userId)) }
                            )
                            NotificationEvent.ShowDeclineFailure -> snackbarController.showAsync(message = txtRequestDeclineFailed)
                        }
                    }

                    NotificationScreen(
                        state = state.value,
                        onIntent = viewModel::process,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable<MainRouter.AddMentorScreen> {
                    val viewModel = koinViewModel<AddMentorViewModel>()
                    val state = viewModel.state.collectAsStateWithLifecycle(it)

                    val txtFailedSendRequest = stringResource(Res.string.failed_send_request)
                    val txtSendRequest = stringResource(Res.string.send_request)

                    ObserveAsEvents(viewModel.event) { event ->
                        when (event) {
                            AddMentorEvent.CloseScreen -> navController.customPopBackStack()
                            AddMentorEvent.ShowRequestUnsent -> snackbarController.showAsync(
                                message = txtFailedSendRequest,
                                actionLabel = txtSendRequest,
                                onAction = { viewModel.process(AddMentorIntent.OnSendRequestClick) }
                            )
                        }
                    }


                    AddMentorScreen(
                        onIntent = { intent ->
                            viewModel.process(intent)

                            when (intent) {
                                is AddMentorIntent.OnCloseClick -> navController.customPopBackStack()
                                else -> {}
                            }
                        },
                        state = state.value
                    )
                }
            }
        }
    }

}
