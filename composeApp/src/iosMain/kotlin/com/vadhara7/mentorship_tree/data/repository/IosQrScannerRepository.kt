package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.*
import platform.Foundation.NSObject
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

class IosQrScannerRepository : QrScannerRepository {
    override suspend fun scan(): String? = suspendCancellableCoroutine { cont ->
        val session = AVCaptureSession()
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: run {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }
        val input = AVCaptureDeviceInput(device, error = null)
        if (session.canAddInput(input)) session.addInput(input)
        val output = AVCaptureMetadataOutput()
        if (session.canAddOutput(output)) session.addOutput(output)
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
