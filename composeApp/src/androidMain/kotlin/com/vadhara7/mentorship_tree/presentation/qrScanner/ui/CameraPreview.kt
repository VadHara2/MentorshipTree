package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.koin.androidx.compose.koinInject
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import com.vadhara7.mentorship_tree.data.repository.AndroidQrScannerRepository

@Composable
actual fun CameraPreview(modifier: Modifier, onPreviewReady: () -> Unit) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val repository = koinInject<QrScannerRepository>() as AndroidQrScannerRepository

    AndroidView(factory = { previewView }, modifier = modifier)

    LaunchedEffect(previewView) {
        repository.bindPreview(previewView)
        onPreviewReady()
    }
}

