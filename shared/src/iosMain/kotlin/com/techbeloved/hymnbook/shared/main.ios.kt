package com.techbeloved.hymnbook.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.techbeloved.media.PlayerControlViewPreview
import platform.UIKit.UIViewController

public fun MainViewController(): UIViewController =
    ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) { PlayerControlViewPreview() }
