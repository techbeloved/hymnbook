package com.techbeloved.hymnbook.nowplaying

sealed class PlaybackEvent {
    data class Error(val message: String) : PlaybackEvent()
    data class Message(val message: String) : PlaybackEvent()
    object None : PlaybackEvent()
}
