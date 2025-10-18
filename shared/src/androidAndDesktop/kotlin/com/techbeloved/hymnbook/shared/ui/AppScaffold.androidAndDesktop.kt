package com.techbeloved.hymnbook.shared.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

internal actual val navigationArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack

@Composable
internal actual fun NavigateUpButton(onClick: () -> Unit) {
    FilledTonalIconButton(onClick = onClick) {
        Icon(navigationArrowBack, contentDescription = "Navigate up")
    }
}
