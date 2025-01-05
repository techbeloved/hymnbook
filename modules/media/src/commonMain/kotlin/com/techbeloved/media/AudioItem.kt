package com.techbeloved.media

data class AudioItem(
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val mediaId: String,
) {
    fun isMidi() = uri.endsWith(".mid")
}
