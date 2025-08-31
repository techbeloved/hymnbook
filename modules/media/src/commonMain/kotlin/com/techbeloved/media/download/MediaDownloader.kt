package com.techbeloved.media.download

import kotlinx.coroutines.flow.Flow

expect fun getPlatformMediaDownloader(): MediaDownloader

interface MediaDownloader {

    fun download(url: String, destination: String): Flow<MediaDownloadState>
}

sealed interface MediaDownloadState {
    data object Initializing : MediaDownloadState
    data class Downloading(val progress: Float) : MediaDownloadState
    data class Error(val message: String) : MediaDownloadState
    data object Success : MediaDownloadState
}
