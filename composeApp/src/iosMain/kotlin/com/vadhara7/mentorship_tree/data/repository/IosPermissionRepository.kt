package com.vadhara7.mentorship_tree.data.repository

import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository

class IosPermissionRepository : PermissionRepository {
    override fun hasCameraPermission(): Boolean = true
}
