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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vadhara7.mentorship_tree.core.mvi.ObserveAsEvents
import com.vadhara7.mentorship_tree.presentation.addMentor.ui.AddMentorScreen
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorIntent
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorViewModel
import com.vadhara7.mentorship_tree.presentation.auth.ui.AuthScreen
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthEvent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.home.ui.TreeScreen
import com.vadhara7.mentorship_tree.presentation.home.vm.TreeIntent
import com.vadhara7.mentorship_tree.presentation.home.vm.TreeViewModel
import com.vadhara7.mentorship_tree.presentation.notification.ui.NotificationScreen
import com.vadhara7.mentorship_tree.presentation.notification.vm.NotificationViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
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
        }
    ) { paddingValues ->
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

                NotificationScreen(
                    state = state.value,
                    onIntent = viewModel::process,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            composable<MainRouter.AddMentorScreen> {
                val viewModel = koinViewModel<AddMentorViewModel>()
                val state = viewModel.state.collectAsStateWithLifecycle(it)

                AddMentorScreen(
                    onIntent = { intent ->
                        viewModel.process(intent)

                        when (intent) {
                            is AddMentorIntent.OnCloseClick -> navController.popBackStack()
                            else -> {}
                        }
                    },
                    state = state.value
                )
            }
        }
    }

}
