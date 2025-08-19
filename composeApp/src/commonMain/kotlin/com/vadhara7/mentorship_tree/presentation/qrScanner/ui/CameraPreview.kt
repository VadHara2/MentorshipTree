package com.vadhara7.mentorship_tree.presentation.qrScanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraPreview(modifier: Modifier = Modifier, onPreviewReady: () -> Unit)

