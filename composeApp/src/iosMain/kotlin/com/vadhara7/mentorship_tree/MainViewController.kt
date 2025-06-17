package com.vadhara7.mentorship_tree

import androidx.compose.ui.window.ComposeUIViewController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize

fun MainViewController() = ComposeUIViewController { App() }

fun initialise() {
    Firebase.initialize()
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
}