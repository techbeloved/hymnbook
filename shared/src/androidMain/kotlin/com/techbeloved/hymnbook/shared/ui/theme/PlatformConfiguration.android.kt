package com.techbeloved.hymnbook.shared.ui.theme

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

private val mediumContrastRange = 0.34f..0.66f
private val highContrastRange = 0.67f..1.0f

private fun isContrastAvailable(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}

@Composable
internal actual fun platformContrastMode(): ContrastMode {
    val isPreview = LocalInspectionMode.current

    return if (!isPreview && isContrastAvailable()) {
        val context = LocalContext.current
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val contrastLevel = uiModeManager.contrast
        when (contrastLevel) {
            in mediumContrastRange -> ContrastMode.Medium
            in highContrastRange -> ContrastMode.High
            else -> ContrastMode.Default
        }
    } else {
        ContrastMode.Default
    }
}
