package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable
import com.techbeloved.hymnbook.shared.swiftinterop.swiftInteropProvider


@Composable
internal actual fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
) {
    NativeShareButtonOnClick { appShareData ->
        swiftInteropProvider().shareData(data = appShareData)
    }.content()
}
