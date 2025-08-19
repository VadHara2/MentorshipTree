package com.vadhara7.mentorship_tree.data.repository

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object PermissionRequester {
    private var launcher: ActivityResultLauncher<String>? = null
    private var callback: ((Boolean) -> Unit)? = null

    fun register(activity: ComponentActivity) {
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            callback?.invoke(granted)
            callback = null
        }
    }

    fun unregister() {
        launcher = null
        callback = null
    }

    fun requestCameraPermission(cb: (Boolean) -> Unit) {
        val l = launcher ?: run {
            cb(false)
            return
        }
        callback = cb
        l.launch(Manifest.permission.CAMERA)
    }
}
