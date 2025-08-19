package com.vadhara7.mentorship_tree.presentation.addMentor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorIntent
import com.vadhara7.mentorship_tree.presentation.addMentor.vm.AddMentorState
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.add_mentor
import mentorshiptree.composeapp.generated.resources.cancel
import mentorshiptree.composeapp.generated.resources.email
import mentorshiptree.composeapp.generated.resources.message
import mentorshiptree.composeapp.generated.resources.send_request
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import mentorshiptree.composeapp.generated.resources.ic_qr_scanner


@Composable
fun AddMentorScreen(
    modifier: Modifier = Modifier,
    state: AddMentorState,
    onIntent: (AddMentorIntent) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Surface(modifier = modifier.fillMaxSize().systemBarsPadding()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.add_mentor),
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { onIntent(AddMentorIntent.OnEmailInput(it)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text(text = stringResource(Res.string.email)) },
                trailingIcon = {
                    IconButton(onClick = { onIntent(AddMentorIntent.OnScanQrClick) }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_qr_scanner),
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = state.message,
                onValueChange = { onIntent(AddMentorIntent.OnMessageInput(it)) },
                singleLine = false,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(Res.string.message)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onIntent(AddMentorIntent.OnSendRequestClick)
                    }
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().imePadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onIntent(AddMentorIntent.OnCloseClick)

                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(Res.string.cancel))
                }
                FilledTonalButton(
                    enabled = state.isEmailValid,
                    onClick = {
                        onIntent(AddMentorIntent.OnSendRequestClick)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(Res.string.send_request))
                }
            }

        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}