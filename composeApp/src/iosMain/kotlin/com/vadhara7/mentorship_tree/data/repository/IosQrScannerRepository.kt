package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.AVMetadataOutput
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

class IosQrScannerRepository : QrScannerRepository {

    @OptIn(ExperimentalForeignApi::class)
    val session: AVCaptureSession = AVCaptureSession()

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun scan(): String? = suspendCancellableCoroutine { cont ->
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: run {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        if (session.inputs.isEmpty()) {
            val input = AVCaptureDeviceInput(device, error = null)
            if (session.canAddInput(input)) session.addInput(input)
        }

        val output = session.outputs.firstOrNull { it is AVCaptureMetadataOutput } as? AVCaptureMetadataOutput
            ?: AVCaptureMetadataOutput().also { if (session.canAddOutput(it)) session.addOutput(it) }

        val delegate = object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
            override fun captureOutput(
                output: AVCaptureOutput,
                didOutputMetadataObjects: List<*>,
                fromConnection: AVCaptureConnection
            ) {
                val obj = didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject
                val result = obj?.stringValue
                if (!cont.isCompleted) {
                    cont.resume(result)
                    session.stopRunning()
                }
            }
        }
        output.setMetadataObjectsDelegate(delegate, dispatch_get_main_queue())
        output.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
        session.startRunning()
    }
}
