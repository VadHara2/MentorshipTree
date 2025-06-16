package com.vadhara7.mentorship_tree.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberTokenProvider(): AuthTokenProvider {
    val context = LocalContext.current
    return AndroidAuthTokenProvider(context)
}
