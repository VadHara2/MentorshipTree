package com.vadhara7.mentorship_tree.auth

import androidx.compose.runtime.Composable

@Composable
actual fun rememberTokenProvider(): AuthTokenProvider = IosAuthTokenProvider()
