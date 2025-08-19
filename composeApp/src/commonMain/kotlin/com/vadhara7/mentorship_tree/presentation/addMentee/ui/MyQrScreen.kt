package com.vadhara7.mentorship_tree.presentation.addMentee.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.ic_close
import mentorshiptree.composeapp.generated.resources.my_qr_instruction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyQrScreen(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    val user = Firebase.auth.currentUser
    val data by remember { mutableStateOf(user?.email ?: user?.uid.orEmpty()) }
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = "Back",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (data.isNotEmpty()) {
                Image(
                    painter = rememberQrCodePainter(data) {
                        colors {
                            dark = QrBrush.solid(onSurfaceColor)
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
