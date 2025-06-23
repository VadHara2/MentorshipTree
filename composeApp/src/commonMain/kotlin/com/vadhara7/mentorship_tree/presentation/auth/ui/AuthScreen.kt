package com.vadhara7.mentorship_tree.presentation.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthIntent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthState



@Composable
fun AuthScreen(modifier: Modifier = Modifier, onIntent: (AuthIntent) -> Unit, state: AuthState) {

    LaunchedEffect(state.serverId) {
        onIntent(AuthIntent.OnGoogleAuthProvided(false))

        state.serverId?.let {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = it
                )
            )

            onIntent(AuthIntent.OnGoogleAuthProvided(true))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isAuthReady) {
            GoogleButtonUiContainerFirebase(
                onResult = {
                    onIntent(AuthIntent.OnGoogleSignInResult(it))
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