package com.vadhara7.mentorship_tree.presentation.addMentee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun MyQrScreen(modifier: Modifier = Modifier) {
    val user = Firebase.auth.currentUser
    val data = user?.email ?: user?.uid.orEmpty()

    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            if (data.isNotEmpty()) {
                Image(
                    painter = rememberQrCodePainter(data),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
            }
        }
    }
}
