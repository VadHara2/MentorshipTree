package com.vadhara7.mentorship_tree.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vadhara7.mentorship_tree.presentation.auth.ui.AuthScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

//    NavHost(navController = navController, startDestination = MainRouter.AuthScreen) {

//        composable<MainRouter.AuthScreen> {
//            val viewModel = koinV
//            AuthScreen(
//
//            )
//        }

//        composable<MainRouter.SecondScreen> { backStackEntry ->
//
//        }
//    }
}