package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
actual fun CameraPreview(modifier: Modifier, onPreviewReady: () -> Unit) {
    LaunchedEffect(Unit) { onPreviewReady() }
    Box(modifier.fillMaxSize()) {
        Text("Camera preview not available on iOS yet", modifier = Modifier)
    }
}

