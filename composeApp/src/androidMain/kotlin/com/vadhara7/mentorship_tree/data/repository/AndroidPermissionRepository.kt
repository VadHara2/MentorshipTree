package com.vadhara7.mentorship_tree.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import com.vadhara7.mentorship_tree.data.repository.PermissionRequester
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository

class AndroidPermissionRepository(
    private val context: Context
) : PermissionRepository {
    override fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestCameraPermission(): Boolean {
        if (hasCameraPermission()) return true
        return suspendCancellableCoroutine { cont ->
            PermissionRequester.requestCameraPermission { granted ->
                cont.resume(granted)
            }
        }
    }
}
