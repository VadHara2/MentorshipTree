package com.vadhara7.mentorship_tree.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.InvertedLuminanceSource
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.vadhara7.mentorship_tree.domain.repository.QrScannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

class AndroidQrScannerRepository(
    private val context: Context,
) : QrScannerRepository {

    private var previewView: PreviewView? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // Reuse a single ZXing reader with stable hints for better performance
    private val reader: MultiFormatReader by lazy {
        MultiFormatReader().apply { setHints(HINTS) }
    }

    private class EagerLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry(this).apply {
            currentState = Lifecycle.State.RESUMED
        }
        override val lifecycle: Lifecycle get() = registry
    }

    private companion object {
        // Focus on QR for speed; you can add AZTEC/DATA_MATRIX back if needed
        private val HINTS = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.CHARACTER_SET to "UTF-8",
            DecodeHintType.ALSO_INVERTED to true
        )
        private const val TARGET_WIDTH = 1280
        private const val TARGET_HEIGHT = 720
    }

    fun bindPreview(view: PreviewView) {
        previewView = view
    }

    override suspend fun scan(): String? = suspendCancellableCoroutine { cont ->
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val pv = previewView ?: run {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val cleanedUp = AtomicBoolean(false)
            fun cleanup() {
                if (cleanedUp.compareAndSet(false, true)) {
                    try {
                        cameraProvider.unbindAll()
                    } catch (_: Exception) { /* no-op */ }
                }
            }
            cont.invokeOnCancellation { cleanup() }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(Size(TARGET_WIDTH, TARGET_HEIGHT))
                .build()

            analysis.setAnalyzer(cameraExecutor) { image: ImageProxy ->
                try {
                    val result = decode(reader, image)
                    if (result != null && !cont.isCompleted) {
                        analysis.clearAnalyzer()
                        cont.resume(result)
                        cleanup()
                    }
                } finally {
                    // Always close the image, even if decoding fails, to avoid backpressure
                    image.close()
                }
            }

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(pv.surfaceProvider)
            }

            val lifecycleOwner = EagerLifecycleOwner()

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
        }, executor)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun decode(reader: MultiFormatReader, image: ImageProxy): String? {
        // We will read only the Y (luma) plane. ZXing's PlanarYUVLuminanceSource expects a continuous luminance buffer.
        val yPlane = image.planes[0]
        val yBuffer = yPlane.buffer
        yBuffer.rewind()

        fun ByteBuffer.safeGet(dst: ByteArray, offset: Int, length: Int) {
            val len = length.coerceAtMost(remaining())
            get(dst, offset, len)
        }

        val width = image.width
        val height = image.height
        val rowStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride

        // Build a contiguous Y array of size width * height.
        val y = ByteArray(width * height)
        var dstIndex = 0

        if (pixelStride == 1 && rowStride == width) {
            // Fast path: already contiguous
            yBuffer.get(y, 0, width * height)
        } else {
            // Copy row by row, respecting row/pixel strides
            val row = ByteArray(rowStride)
            for (r in 0 until height) {
                yBuffer.position(r * rowStride)
                yBuffer.safeGet(row, 0, rowStride)
                var srcPos = 0
                repeat(width) {
                    y[dstIndex++] = row[srcPos]
                    srcPos += pixelStride
                }
            }
        }

        // Create luminance source
        var source: LuminanceSource = PlanarYUVLuminanceSource(
            y, /* y data */
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )

        // Handle rotation reported by CameraX (preview may be rotated, but buffers keep sensor orientation)
        if (source.isRotateSupported) {
            when (image.imageInfo.rotationDegrees) {
                90  -> source = source.rotateCounterClockwise()
                180 -> source = source.rotateCounterClockwise().rotateCounterClockwise()
                270 -> source = source.rotateCounterClockwise().rotateCounterClockwise().rotateCounterClockwise()
            }
        }

        fun tryDecode(src: LuminanceSource): String? {
            val attempts = listOf(
                { BinaryBitmap(HybridBinarizer(src)) },
                { BinaryBitmap(GlobalHistogramBinarizer(src)) },
                {
                    val inv = InvertedLuminanceSource(src)
                    BinaryBitmap(HybridBinarizer(inv))
                },
                {
                    val inv = InvertedLuminanceSource(src)
                    BinaryBitmap(GlobalHistogramBinarizer(inv))
                }
            )
            for (mk in attempts) {
                try {
                    val text = reader.decode(mk()).text
                    return text
                } catch (_: Exception) {
                    reader.reset()
                }
            }
            return null
        }
        return tryDecode(source)
    }
}
