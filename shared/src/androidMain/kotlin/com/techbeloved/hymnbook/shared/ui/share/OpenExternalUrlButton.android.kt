package com.techbeloved.hymnbook.shared.ui.share

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
internal actual fun OpenExternalUrlButton(content: @Composable (NativeExternalUrlClick.() -> Unit)) {
    val localContext = LocalContext.current
    NativeExternalUrlClick { url ->
        val openIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        try {
            localContext.startActivity(openIntent)
        } catch (_: ActivityNotFoundException) {

        }
    }.content()
}
