package com.vadhara7.mentorship_tree.domain.repository

expect interface PermissionRepository {
    fun hasCameraPermission(): Boolean
}
