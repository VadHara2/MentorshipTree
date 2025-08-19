package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.material3.Button
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
    if (state.hasPermission) {
        Text(text = state.result ?: "Scanning...", modifier = modifier)
    } else {
        Button(onClick = { onIntent(QrScannerIntent.StartScanning) }, modifier = modifier) {
            Text(text = "Camera permission required")
        }
    }
}

