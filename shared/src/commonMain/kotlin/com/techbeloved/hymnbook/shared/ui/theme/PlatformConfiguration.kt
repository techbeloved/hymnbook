package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.runtime.Composable

@Composable
internal expect fun platformContrastMode(): ContrastMode

internal enum class ContrastMode {
    Default,
    Medium,
    High
}
