package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.runtime.Composable

public interface VoiceRecognitionLauncher {
    public fun launch()
}

@Composable
public expect fun rememberVoiceRecognitionLauncher(onResult: (String?) -> Unit): VoiceRecognitionLauncher
