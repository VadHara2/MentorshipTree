package com.vadhara7.mentorship_tree.domain.repository

actual interface PermissionRepository {
    actual fun hasCameraPermission(): Boolean
    actual suspend fun requestCameraPermission(): Boolean
}
