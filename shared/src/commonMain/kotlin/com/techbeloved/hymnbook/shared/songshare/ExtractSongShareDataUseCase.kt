package com.techbeloved.hymnbook.shared.songshare

import io.ktor.http.Url
import me.tatarka.inject.annotations.Inject

internal class ExtractSongShareDataUseCase @Inject constructor() {
    operator fun invoke(url: String): SongShareIncomingData? = Url(url).run {
        when {
            parameters.isEmpty() -> null
            else -> SongShareIncomingData(
                songbook = parameters["songbook"],
                songEntry = parameters["entry"],
                songQuery = parameters["q"],
            )
        }
    }
}
