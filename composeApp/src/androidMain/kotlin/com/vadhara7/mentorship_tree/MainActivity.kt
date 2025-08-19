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
        PermissionRequester.register(this)

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
        PermissionRequester.unregister()
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}