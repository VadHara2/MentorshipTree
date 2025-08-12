package com.vadhara7.mentorship_tree.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vadhara7.mentorship_tree.core.mvi.ObserveAsEvents
import com.vadhara7.mentorship_tree.presentation.auth.ui.AuthScreen
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthEvent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.home.ui.HomeScreen
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeViewModel
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

    val hideRoutesForBottomBar = listOf(
        MainRouter.AuthScreen,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val shouldDisplayBottomBar = hideRoutesForBottomBar.any { route ->
        route::class.qualifiedName != currentDestination
    }

    Scaffold(
        bottomBar = {
            if (shouldDisplayBottomBar) {
                NavigationBar {
                    navigationPages.forEach {
                        val isSelected = currentDestination == it::class.qualifiedName

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
                val viewModel = koinViewModel<HomeViewModel>()
                val state = viewModel.state.collectAsStateWithLifecycle()

                HomeScreen(
                    state = state.value,
                    onIntent = viewModel::process,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            composable<MainRouter.NotificationScreen> {

            }
        }
    }

}

