package com.vadhara7.mentorship_tree

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.vadhara7.mentorship_tree.secrets.SecretsRepository
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = MainRouter.FirstScreen) {

            composable<MainRouter.FirstScreen> {
                var authReady by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    val repo = SecretsRepository()
                    val id = repo.getGoogleAuthServerId()


                    GoogleAuthProvider.create(
                        credentials = GoogleAuthCredentials(
                            serverId = id
                        )
                    )
                    authReady = true
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (authReady) {
                        GoogleButtonUiContainerFirebase(
                            onResult = {
                                println("Result: $it")
                            },
                            linkAccount = false
                        ) {

                            Button(onClick = { this.onClick() }) {
                                Text("Sign In with Google")
                            }

                        }
                    }


                }
            }

            composable<MainRouter.SecondScreen> { backStackEntry ->
                val args = backStackEntry.toRoute<MainRouter.SecondScreen>()
                SecondScreen(args.text) {
                    navController.popBackStack()
                }
            }
        }
    }
}
