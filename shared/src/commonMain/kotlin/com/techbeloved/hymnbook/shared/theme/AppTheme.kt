package com.techbeloved.hymnbook.shared.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// Configure the theme and colors

@Composable
internal fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
    )
}
