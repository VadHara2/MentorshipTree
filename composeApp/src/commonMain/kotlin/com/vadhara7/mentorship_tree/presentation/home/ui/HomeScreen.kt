package com.vadhara7.mentorship_tree.presentation.home.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vadhara7.mentorship_tree.presentation.commonComponents.Background
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeIntent
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeState
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.sign_out
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onIntent: (HomeIntent) -> Unit, state: HomeState) {
    Background(modifier = modifier) {
        Text(
            text = state.userName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )

        Button(
            onClick = {
                onIntent(HomeIntent.OnSignOutClick)
            },
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    stringResource(Res.string.sign_out),
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }
    }
}
