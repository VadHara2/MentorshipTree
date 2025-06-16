package com.vadhara7.mentorship_tree

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = MainRouter.FirstScreen, modifier = Modifier.fillMaxSize().safeContentPadding()) {

            composable<MainRouter.FirstScreen> {
                var showContent by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {

                        navController.navigate(MainRouter.SecondScreen("qwerty"))
                    }) {
                        Text("Open Second Screen")
                    }
                    Button(onClick = { showContent = !showContent }) {
                        Text("Click me!")
                    }
                    AnimatedVisibility(showContent) {
                        val greeting = remember { Greeting().greet() }
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(painterResource(Res.drawable.compose_multiplatform), null)
                            Text("Compose: $greeting")
                        }
                    }
                }
            }

            composable<MainRouter.SecondScreen> { backStackEntry ->
                val args = backStackEntry.toRoute<MainRouter.SecondScreen>()
                SecondScreen(args.text)
            }
        }
    }
}
