package com.vadhara7.mentorship_tree.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vadhara7.mentorship_tree.core.mvi.ObserveAsEvents
import com.vadhara7.mentorship_tree.presentation.auth.ui.AuthScreen
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthEvent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthViewModel
import com.vadhara7.mentorship_tree.presentation.home.ui.HomeScreen
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val user by Firebase.auth.authStateChanged
        .collectAsStateWithLifecycle(initialValue = null)

    NavHost(
        navController = navController,
        startDestination = if (user == null) MainRouter.AuthScreen else MainRouter.HomeScreen,
        modifier = modifier
    ) {

        composable<MainRouter.AuthScreen> {
            val viewModel = koinViewModel<AuthViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle()

            ObserveAsEvents(viewModel.event) { event ->
                when (event) {
                    is AuthEvent.NavigateToHomeScreen -> {
                        navController.navigate(MainRouter.HomeScreen)
                    }
                }
            }

            AuthScreen(
                state = state.value,
                onIntent = viewModel::process
            )
        }


        composable<MainRouter.HomeScreen> {
            val viewModel = koinViewModel<HomeViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle()

            HomeScreen(
                state = state.value,
                onIntent = viewModel::process
            )
        }
    }
}