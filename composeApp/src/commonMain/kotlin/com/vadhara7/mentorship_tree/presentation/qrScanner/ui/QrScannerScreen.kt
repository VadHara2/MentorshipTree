package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.vadhara7.mentorship_tree.presentation.qrScanner.vm.QrScannerIntent
import com.vadhara7.mentorship_tree.presentation.qrScanner.vm.QrScannerState

@Composable
fun QrScannerScreen(
    modifier: Modifier = Modifier,
    state: QrScannerState,
    onIntent: (QrScannerIntent) -> Unit
) {
    LaunchedEffect(Unit) { onIntent(QrScannerIntent.StartScanning) }
    Text(text = state.result ?: "Scanning...", modifier = modifier)
}

