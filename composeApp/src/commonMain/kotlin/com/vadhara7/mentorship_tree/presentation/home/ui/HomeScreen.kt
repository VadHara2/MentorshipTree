package com.vadhara7.mentorship_tree.presentation.home.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vadhara7.mentorship_tree.presentation.commonComponents.Background
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeIntent
import com.vadhara7.mentorship_tree.presentation.home.vm.HomeState

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onIntent: (HomeIntent) -> Unit, state: HomeState) {
    Background(modifier = modifier) {
        Text(
            text = state.userName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
