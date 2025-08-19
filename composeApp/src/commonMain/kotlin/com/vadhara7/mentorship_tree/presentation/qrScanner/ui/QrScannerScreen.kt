package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Alignment
import com.vadhara7.mentorship_tree.presentation.qrScanner.vm.QrScannerIntent
import com.vadhara7.mentorship_tree.presentation.qrScanner.vm.QrScannerState

@Composable
fun QrScannerScreen(
    modifier: Modifier = Modifier,
    state: QrScannerState,
    onIntent: (QrScannerIntent) -> Unit
) {
    if (state.hasPermission) {
        Box(modifier.fillMaxSize().systemBarsPadding()) {
            CameraPreview(modifier = Modifier.fillMaxSize()) {
                onIntent(QrScannerIntent.StartScanning)
            }
            Text(
                text = state.result ?: "Scanning...",
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    } else {
        Button(onClick = { onIntent(QrScannerIntent.StartScanning) }, modifier = modifier) {
            Text(text = "Camera permission required")
        }
    }
}

