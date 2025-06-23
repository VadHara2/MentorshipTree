package com.vadhara7.mentorship_tree

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.vadhara7.mentorship_tree.core.di.commonModule
import com.vadhara7.mentorship_tree.presentation.navigation.NavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication


@Composable
@Preview
fun App() = MaterialTheme {
    KoinApplication(
        application = {
            modules(commonModule)
        }
    ) {
        NavGraph()
    }
}

