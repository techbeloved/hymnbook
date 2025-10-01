package com.techbeloved.hymnbook.shared.ui.share

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun OpenExternalUrlButton(content: @Composable (NativeExternalUrlClick.() -> Unit)) {
    NativeExternalUrlClick { url ->
        UIApplication.sharedApplication.apply {
            val nsUrl = NSURL.URLWithString(url)
            if (nsUrl != null && canOpenURL(nsUrl)) {
                openURL(nsUrl, options = emptyMap<Any?, Any>()) { isSuccess ->

                }
            }
        }
    }.content()
}
