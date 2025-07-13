package com.techbeloved.sheetmusic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
public class SheetMusicState {
    public var isZooming: Boolean by mutableStateOf(false)
        internal set
}

@Composable
public fun rememberSheetMusicState(): SheetMusicState = SheetMusicState()
