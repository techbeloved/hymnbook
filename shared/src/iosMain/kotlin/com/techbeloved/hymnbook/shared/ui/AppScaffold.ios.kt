package com.techbeloved.hymnbook.shared.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

internal actual val navigationArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBackIos

@Composable
internal actual fun NavigateUpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(navigationArrowBack, contentDescription = "Navigate up")
    }
}
