package com.techbeloved.hymnbook.shared.ui.share

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun NativeShareButton(
    content: @Composable NativeShareButtonOnClick.() -> Unit,
) {
    val localContext = LocalContext.current
    NativeShareButtonOnClick { shareAppData ->
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, shareAppData.title)
            val shareText = """
                ${shareAppData.description}
                ${shareAppData.url}
            """.trimIndent()
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        localContext.startActivity(Intent.createChooser(intent, "Share via"))
    }.content()
}
