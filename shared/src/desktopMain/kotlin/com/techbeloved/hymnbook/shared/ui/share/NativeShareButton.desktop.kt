package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable

@Composable
internal actual fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
) {
    NativeShareButtonOnClick {

    }.content()
}
