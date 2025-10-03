package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.uikit.LocalUIView
import com.techbeloved.hymnbook.shared.swiftinterop.swiftInteropProvider


@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
) {
    val localView = LocalUIView.current
    NativeShareButtonOnClick { appShareData ->
        swiftInteropProvider().shareData(data = appShareData, sourceView = localView)
    }.content()
}
