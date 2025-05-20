package com.techbeloved.hymnbook.shared.ui.appbar

import androidx.compose.ui.graphics.vector.ImageVector
import com.techbeloved.hymnbook.shared.ui.home.TopLevelDestination

internal data class HomeNavItem(
    val label: String,
    val icon: ImageVector,
    val route: TopLevelDestination,
)
