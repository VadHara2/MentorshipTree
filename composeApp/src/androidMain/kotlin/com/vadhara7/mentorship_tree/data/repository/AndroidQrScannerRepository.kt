package com.vadhara7.mentorship_tree.data.repository

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.PlanarYUVLuminanceSource
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlin.coroutines.resume

class AndroidQrScannerRepository(
    private val context: Context,
) : QrScannerRepository {

    override suspend fun scan(): String? = suspendCancellableCoroutine { cont ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val reader = MultiFormatReader().apply {
                setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(executor) { image: ImageProxy ->
                val result = decode(reader, image)
                if (result != null && !cont.isCompleted) {
                    cont.resume(result)
                    analysis.clearAnalyzer()
                    cameraProvider.unbindAll()
                }
                image.close()
            }

            val lifecycleOwner = object : LifecycleOwner {
                private val registry = LifecycleRegistry(this)
                init { registry.currentState = Lifecycle.State.RESUMED }
                override fun getLifecycle(): Lifecycle = registry
            }

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                analysis
            )
        }, executor)
    }

    private fun decode(reader: MultiFormatReader, image: ImageProxy): String? {
        val buffer = image.planes.first().buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val source: LuminanceSource = PlanarYUVLuminanceSource(
            bytes,
            image.width,
            image.height,
            0,
            0,
            image.width,
            image.height,
            false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        return try {
            reader.decode(bitmap).text
        } catch (e: Exception) {
            null
        } finally {
            reader.reset()
        }
    }
}
