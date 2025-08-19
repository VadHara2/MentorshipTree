package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.vadhara7.mentorship_tree.data.repository.AndroidQrScannerRepository
import org.koin.compose.koinInject

@Composable
actual fun CameraPreview(modifier: Modifier, onPreviewReady: () -> Unit) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val repository: AndroidQrScannerRepository = koinInject()

    AndroidView(factory = { previewView }, modifier = modifier)

    LaunchedEffect(previewView) {
        repository.bindPreview(previewView)
        onPreviewReady()
    }
}

