package com.techbeloved.hymnbook.sheetmusic

sealed class SheetMusicState {
    data class Ready(val id: Int, val title: String, val localUri: String?, val darkMode: Boolean = false) : SheetMusicState()
    data class Downloading(val id: Int, val title: String, val downloadProgress: Int) : SheetMusicState()
    data class NotDownloaded(val id: Int, val title: String, val remoteUri: String?) : SheetMusicState()
    data class DownloadFailed(val id: Int, val title: String, val remoteUri: String?) : SheetMusicState()
}