package com.vadhara7.mentorship_tree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vadhara7.mentorship_tree.data.repository.PermissionRequester

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        PermissionRequester.activity = this

        setContent {
            enableEdgeToEdge(
                statusBarStyle = if (isSystemInDarkTheme()) {
                    SystemBarStyle.dark(0)

                } else {
                    SystemBarStyle.light(0, 0)
                }
            )

            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PermissionRequester.activity = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionRequester.onRequestPermissionsResult(requestCode, grantResults)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}