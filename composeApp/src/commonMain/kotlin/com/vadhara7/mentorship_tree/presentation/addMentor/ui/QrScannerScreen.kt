package com.vadhara7.mentorship_tree.presentation.addMentor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.ic_close

@Composable
fun QrScannerScreen(
    modifier: Modifier = Modifier,
    onResult: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalButton(onClick = { onResult("scanned@example.com") }) {
                Text("Simulate Scan")
            }
            IconButton(onClick = onCloseClick, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = "Back",
                )
            }
        }
    }
}
