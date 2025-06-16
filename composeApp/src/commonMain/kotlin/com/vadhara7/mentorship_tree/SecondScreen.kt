package com.vadhara7.mentorship_tree

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SecondScreen(args: SecondScreenArgs) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(args.message)
    }
}

