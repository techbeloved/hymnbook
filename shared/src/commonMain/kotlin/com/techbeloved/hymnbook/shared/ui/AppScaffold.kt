@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.techbeloved.hymnbook.shared.ui.navigation.LocalNavController

internal expect val navigationArrowBack: ImageVector

@Composable
internal fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    titleContent: @Composable (() -> Unit)? = null,
    showUpButton: Boolean = true,
    scrollBehaviour: TopAppBarScrollBehavior? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = if (titleContent != null) {
            titleContent
        } else {
            {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            if (showUpButton) {
                val navigator = LocalNavController.current
                IconButton(onClick = { navigator?.navigateUp() }) {
                    Icon(navigationArrowBack, contentDescription = "Navigate up")
                }
            }
        },
        actions = actions,
        modifier = modifier,
        scrollBehavior = scrollBehaviour,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        windowInsets = windowInsets,
    )
}

@Composable
internal fun CenteredAppTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    titleContent: @Composable (() -> Unit)? = null,
    showUpButton: Boolean = true,
    scrollBehaviour: TopAppBarScrollBehavior? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    CenterAlignedTopAppBar(
        title = if (titleContent != null) {
            titleContent
        } else {
            {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            if (showUpButton) {
                val navigator = LocalNavController.current
                IconButton(onClick = { navigator?.navigateUp() }) {
                    Icon(navigationArrowBack, contentDescription = "Navigate up")
                }
            }
        },
        actions = actions,
        modifier = modifier,
        scrollBehavior = scrollBehaviour,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        windowInsets = windowInsets,
    )
}
