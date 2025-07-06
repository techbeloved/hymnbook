package com.techbeloved.hymnbook.shared.songbooks

import com.techbeloved.hymnbook.shared.preferences.PreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import me.tatarka.inject.annotations.Inject

internal class GetPreferredSongbookUseCase @Inject constructor(
    private val preferenceRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): String? =
        preferenceRepository.getPreferenceFlow(SongbookPreferenceKey)
            .firstOrNull()?.ifBlank { null }
}
