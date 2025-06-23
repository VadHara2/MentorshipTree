package com.vadhara7.mentorship_tree.presentation.navigation

import kotlinx.serialization.Serializable

object MainRouter {

    @Serializable
    data object FirstScreen

    @Serializable
    data class SecondScreen(val text: String)
}