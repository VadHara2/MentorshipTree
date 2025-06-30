package com.vadhara7.mentorship_tree.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {


            Text(
                text = state.userName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Button(
                onClick = {
                    onIntent(HomeIntent.OnSignOutClick)
                },
                shape = MaterialTheme.shapes.extraLarge,
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

            TextField(
                value = state.mentorEmail,
                onValueChange = {
                    onIntent(HomeIntent.OnMentorEmailChange(it))
                }
            )

            Button(
                onClick = {
                    onIntent(HomeIntent.OnSendRequestClick)
                }
            ) {
                Text(text = "Send Request")
            }

            Text(text = "Requests:", color = MaterialTheme.colorScheme.onBackground)

            state.requests.forEach {
                Column {
                    Text(text = it.menteeId, color = MaterialTheme.colorScheme.onBackground)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = {
                            onIntent(HomeIntent.OnApproveRequest(it))
                        }) {
                            Text(text = "Approve")
                        }

                        Button(
                            onClick = {
                                onIntent(HomeIntent.OnRejectRequest(it))
                            }
                        ) {
                            Text(text = "Reject")
                        }
                    }

                }


            }


        }
    }
}
