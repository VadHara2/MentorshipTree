package com.vadhara7.mentorship_tree.data.repository

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.Manifest

object PermissionRequester {
    private const val REQUEST_CODE = 1001
    private var callback: ((Boolean) -> Unit)? = null
    var activity: Activity? = null

    fun requestCameraPermission(cb: (Boolean) -> Unit) {
        val act = activity ?: run {
            cb(false)
            return
        }
        callback = cb
        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE)
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            callback?.invoke(granted)
            callback = null
        }
    }
}
