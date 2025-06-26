package com.vadhara7.mentorship_tree.presentation.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthIntent
import com.vadhara7.mentorship_tree.presentation.auth.vm.AuthState
import dev.gitlive.firebase.auth.FirebaseUser
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.app_name
import mentorshiptree.composeapp.generated.resources.continue_with_google
import mentorshiptree.composeapp.generated.resources.ic_google_color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(modifier: Modifier = Modifier, onIntent: (AuthIntent) -> Unit, state: AuthState) {

    LaunchedEffect(state.serverId) {

        state.serverId?.let {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = it
                )
            )

            onIntent(AuthIntent.OnGoogleAuthProvided(true))
        }
    }

    Background {

        Title(modifier = Modifier.fillMaxHeight(.5f).fillMaxWidth())

        if (state.isLoading) {
            LoadingIndicator(
                modifier = Modifier.size(164.dp).align(Alignment.Center)
            )
        }

        if (state.isAuthReady) {
            GoogleSignInButton(
                modifier = Modifier.align(Alignment.Center),
                isLoading = state.isLoading,
                onFirebaseResult = { onIntent(AuthIntent.OnGoogleSignInResult(it)) },
                onClick = { onIntent(AuthIntent.OnGoogleSignInClick) }
            )
        }

    }
}

@Composable
private fun Background(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        content = content
    )
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onFirebaseResult: (Result<FirebaseUser?>) -> Unit,
    onClick: () -> Unit
) {
    GoogleButtonUiContainerFirebase(
        onResult = {
            onFirebaseResult(it)
        },
        linkAccount = false,
        modifier = modifier
    ) {

        if (isLoading) return@GoogleButtonUiContainerFirebase

        Button(
            onClick = {
                onClick()
                this.onClick()
            },
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier.padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_google_color),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(Res.string.continue_with_google),
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }

    }
}
