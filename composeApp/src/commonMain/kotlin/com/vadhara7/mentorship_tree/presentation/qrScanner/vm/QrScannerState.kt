package com.vadhara7.mentorship_tree.presentation.qrScanner.vm

import com.vadhara7.mentorship_tree.core.mvi.*

data class QrScannerState(
    val result: String? = null,
    val hasPermission: Boolean = true
) : State

sealed interface QrScannerIntent : Intent {
    data object StartScanning : QrScannerIntent
}

sealed interface QrScannerEffect : Effect {
    data class OnResult(val text: String?) : QrScannerEffect
    data object PermissionDenied : QrScannerEffect
}

sealed interface QrScannerEvent : Event {
    data class OnScanned(val text: String) : QrScannerEvent
    data object OnPermissionDenied : QrScannerEvent
}

