package com.techbeloved.hymnbook.shared.media

internal expect val platformSupportedAudioFormats: List<String>

internal val commonAudioFormats = listOf("mp3", "aac", "m4a", "mid")

internal val supportedAudioFormats get() = platformSupportedAudioFormats + commonAudioFormats
