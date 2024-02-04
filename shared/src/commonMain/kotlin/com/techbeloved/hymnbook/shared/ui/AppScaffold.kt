@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
internal fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showUpButton: Boolean = true,
    scrollBehaviour: TopAppBarScrollBehavior? = null,
    actions: @Composable() (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showUpButton) {
                val navigator = LocalNavigator.currentOrThrow
                IconButton(onClick = navigator::pop) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Navigate up")
                }
            }
        },
        actions = actions,
        modifier = modifier,
        scrollBehavior = scrollBehaviour,
    )
}
