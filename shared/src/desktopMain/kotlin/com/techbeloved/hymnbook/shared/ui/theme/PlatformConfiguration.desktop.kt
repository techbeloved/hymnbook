package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun platformContrastMode(): ContrastMode  = ContrastMode.Default

@Composable
internal actual fun isAppInDarkTheme(): Boolean  = isSystemInDarkTheme()
