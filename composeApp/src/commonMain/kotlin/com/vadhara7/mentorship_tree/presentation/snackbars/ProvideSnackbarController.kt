package com.vadhara7.mentorship_tree.presentation.snackbars

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalSnackbarController = staticCompositionLocalOf<SnackbarController> {
    error("LocalSnackbarController not provided")
}

class SnackbarController(
    private val host: SnackbarHostState,
    private val scope: CoroutineScope
) {
    /**
     * Suspend version — use inside LaunchedEffect / coroutine.
     */
    suspend fun show(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarResult = host.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = duration
    )

    /**
     * Fire-and-forget version — safe to call directly from Composables / callbacks.
     */
    fun showAsync(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ) {
        Logger.i("showAsync")
        scope.launch {
            val result = host.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
            Logger.i("result: ${result}")
            if (result == SnackbarResult.ActionPerformed) {
                onAction?.invoke()
            }
        }
    }
}

@Composable
fun ProvideSnackbarController(
    hostState: SnackbarHostState,
    content: @Composable (SnackbarController) -> Unit
) {
    val scope = rememberCoroutineScope()
    val controller = remember(hostState, scope) { SnackbarController(hostState, scope) }
    CompositionLocalProvider(LocalSnackbarController provides controller) {
        content(controller)
    }
}