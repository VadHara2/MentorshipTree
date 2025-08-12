package com.vadhara7.mentorship_tree

import androidx.compose.runtime.Composable
import com.vadhara7.mentorship_tree.core.di.commonModule
import com.vadhara7.mentorship_tree.navigation.NavGraph
import com.vadhara7.mentorship_tree.theme.MentorshipTreeTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication


@Composable
@Preview
fun App() = MentorshipTreeTheme {
    KoinApplication(
        application = {
            modules(commonModule)
        }
    ) {
        NavGraph()
    }
}

