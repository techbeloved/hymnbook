package com.techbeloved.hymnbook.shared.soundfont

import com.techbeloved.apiclient.ApiClient
import com.techbeloved.hymnbook.shared.model.soundfont.OnlineSoundFont
import com.techbeloved.hymnbook.shared.model.soundfont.OnlineSoundFontsResponse
import me.tatarka.inject.annotations.Inject

private const val SoundFontsUrl = "https://app.watchmanmusic.com/api/soundfonts.json"

internal class GetOnlineSoundFontsUseCase @Inject constructor(
    private val apiClient: ApiClient,
) {
    suspend operator fun invoke(): List<OnlineSoundFont> =
        runCatching { apiClient.apiGet<OnlineSoundFontsResponse>(SoundFontsUrl).soundfonts }
            .getOrElse { emptyList() }
}
