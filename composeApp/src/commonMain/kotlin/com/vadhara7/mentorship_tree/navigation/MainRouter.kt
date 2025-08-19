package com.vadhara7.mentorship_tree.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.ic_notifications
import mentorshiptree.composeapp.generated.resources.ic_tree
import mentorshiptree.composeapp.generated.resources.notifications
import mentorshiptree.composeapp.generated.resources.tree
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

object MainRouter {

    @Serializable
    data object AuthScreen

    @Serializable
    data object TreeScreen : NavigationPage(
        titleRes = Res.string.tree,
        iconRes = Res.drawable.ic_tree
    )

    @Serializable
    data object NotificationScreen : NavigationPage(
        titleRes = Res.string.notifications,
        iconRes = Res.drawable.ic_notifications
    )

    @Serializable
    data object AddMentorScreen

    @Serializable
    data object MyQrScreen
}


@Serializable
sealed class NavigationPage(
    @Contextual val titleRes: StringResource,
    @Contextual val iconRes: DrawableResource
)