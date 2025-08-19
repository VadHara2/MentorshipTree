package com.vadhara7.mentorship_tree.presentation.addMentee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.options.QrBrush
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.my_qr_instruction
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyQrScreen(modifier: Modifier = Modifier) {
    val user = Firebase.auth.currentUser
    val data = user?.email ?: user?.uid.orEmpty()

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (data.isNotEmpty()) {
                Image(
                    painter = rememberQrCodePainter(data) {
                        colors {
                            dark = QrBrush.solid(MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.my_qr_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}
