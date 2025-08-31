package com.techbeloved.hymnbook.shared.songshare

import io.ktor.http.Url
import me.tatarka.inject.annotations.Inject

internal class ExtractSongShareDataUseCase @Inject constructor() {
    operator fun invoke(url: String): SongShareData? = Url(url).run {
        when {
            parameters.isEmpty() -> null
            else -> SongShareData(
                songbook = parameters["songbook"],
                songEntry = parameters["entry"],
                songQuery = parameters["q"],
            )
        }
    }
}
