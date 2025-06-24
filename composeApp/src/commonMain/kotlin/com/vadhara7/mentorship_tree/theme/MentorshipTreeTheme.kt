package com.vadhara7.mentorship_tree.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MentorshipTreeTheme(content: @Composable () -> Unit) {

    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }



    MaterialTheme(content = content, colorScheme = colorScheme)
}