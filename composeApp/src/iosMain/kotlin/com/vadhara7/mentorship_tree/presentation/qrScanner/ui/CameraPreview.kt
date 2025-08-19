package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.vadhara7.mentorship_tree.data.repository.IosQrScannerRepository
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import org.koin.compose.koinInject
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.UIKit.UIView
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreview(modifier: Modifier, onPreviewReady: () -> Unit) {
    val repository = koinInject<QrScannerRepository>() as IosQrScannerRepository
    val previewLayer = remember {
        AVCaptureVideoPreviewLayer(session = repository.session).apply {
            videoGravity = AVLayerVideoGravityResizeAspectFill
        }
    }
    UIKitView(
        factory = {
            UIView().apply { layer.addSublayer(previewLayer) }
        },
        update = { view ->
            previewLayer.setFrame(view.bounds)
        },
        modifier = modifier
    )
    LaunchedEffect(Unit) { onPreviewReady() }
}

