package com.vadhara7.mentorship_tree.auth

import androidx.compose.runtime.Composable

/** Returns an instance of [AuthTokenProvider] for the current platform. */
@Composable
expect fun rememberTokenProvider(): AuthTokenProvider
