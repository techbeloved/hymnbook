package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable

@Composable
internal expect fun OpenExternalUrlButton(
    content: @Composable NativeExternalUrlClick.() -> Unit,
)

internal fun interface NativeExternalUrlClick {
    fun onClick(url: String)
}
