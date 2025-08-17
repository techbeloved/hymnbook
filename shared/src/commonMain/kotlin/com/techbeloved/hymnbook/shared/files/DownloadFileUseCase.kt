package com.techbeloved.hymnbook.shared.files

import com.techbeloved.media.download.MediaDownloader
import me.tatarka.inject.annotations.Inject

internal class DownloadFileUseCase @Inject constructor(
    private val downloader: MediaDownloader,
) {
    operator fun invoke(url: String, destination: String) = downloader.download(url, destination)
}
