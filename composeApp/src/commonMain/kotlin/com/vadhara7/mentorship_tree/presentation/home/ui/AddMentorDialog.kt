package com.vadhara7.mentorship_tree.presentation.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.add_mentor
import mentorshiptree.composeapp.generated.resources.cancel
import mentorshiptree.composeapp.generated.resources.email
import mentorshiptree.composeapp.generated.resources.send_request
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddMentorDialog(
    onDismiss: () -> Unit,
    onScanQrClick: () -> Unit = {},
    onConfirmWithMessage: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    val isValid by remember(email) { mutableStateOf(email.isValidEmail()) }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(Res.string.add_mentor)) },
        text = {
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    label = { Text(text = stringResource(Res.string.email)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val e = email.trim().lowercase()
                            if (e.isValidEmail()) {
                                onConfirmWithMessage.invoke(e, message.trim())
                            }
                        }
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onScanQrClick) {
                        Text("Scan QR")
                    }
                }
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Message") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val e = email.trim().lowercase()
                            if (e.isValidEmail()) {
                                onConfirmWithMessage.invoke(e, message.trim())
                            }
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    val e = email.trim().lowercase()
                    onConfirmWithMessage.invoke(e, message.trim())
                }
            ) { Text(text = stringResource(Res.string.send_request)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
        }
    )

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

/** Простий кросплатформний валідатор для commonMain */
private fun String.isValidEmail(): Boolean {
    val s = trim()
    val at = s.indexOf('@')
    if (at <= 0 || at == s.lastIndex) return false
    val dot = s.indexOf('.', startIndex = at + 2) // щонайменше 1 символ між '@' та '.'
    return dot in (at + 2)..<s.lastIndex
}
