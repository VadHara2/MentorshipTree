package com.vadhara7.mentorship_tree.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    onBack: () -> Unit,
    tokenProvider: AuthTokenProvider,
    repository: AuthRepository = FirebaseAuthRepository()
) {
    val vm: AuthViewModel = viewModel(factory = androidx.lifecycle.viewmodel.compose.viewModelFactory {
        AuthViewModel(repository, tokenProvider)
    })

    val state by vm.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        when (state) {
            AuthState.Idle -> Text("Please sign in")
            AuthState.Loading -> Text("Loading...")
            is AuthState.Error -> Text((state as AuthState.Error).message)
            is AuthState.Success -> Text("User: ${(state as AuthState.Success).userId}")
        }
        Button(onClick = { vm.onIntent(AuthIntent.SignInWithGoogle) }) {
            Text("Sign in with Google")
        }
        Button(onClick = { vm.onIntent(AuthIntent.SignInWithApple) }) {
            Text("Sign in with Apple")
        }
        Button(onClick = { vm.onIntent(AuthIntent.SignOut) }) {
            Text("Sign out")
        }
        Button(onClick = onBack) { Text("Back") }
    }
}
