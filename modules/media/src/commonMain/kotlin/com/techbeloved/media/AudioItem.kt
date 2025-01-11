package com.techbeloved.media

data class AudioItem(
    val absolutePath: String,
    val relativePath: String,
    val title: String,
    val artist: String,
    val album: String,
    val mediaId: String,
) {
    fun isMidi() = absolutePath.endsWith(".mid")
}
