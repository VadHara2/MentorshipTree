package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVCaptureDevice
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IosPermissionRepository : PermissionRepository {
    override fun hasCameraPermission(): Boolean {
        return AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
    }

    override suspend fun requestCameraPermission(): Boolean = suspendCoroutine { cont ->
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
            cont.resume(granted)
        }
    }
}
