package com.vadhara7.mentorship_tree.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.vadhara7.mentorship_tree.domain.repository.PermissionRepository

class AndroidPermissionRepository(
    private val context: Context
) : PermissionRepository {
    override fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
