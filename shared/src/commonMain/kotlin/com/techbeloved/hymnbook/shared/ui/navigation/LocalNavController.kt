package com.techbeloved.hymnbook.shared.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

internal val LocalNavController = compositionLocalOf<NavHostController?> { null }

internal val ProvidableCompositionLocal<NavHostController?>.currentOrThrow
    @Composable
    get() = checkNotNull(current)
