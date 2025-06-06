package com.techbeloved.hymnbook.shared.songs

import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.titles.GetFilteredSongTitlesUseCase
import me.tatarka.inject.annotations.Inject

internal class GetSongIdsByFilterUseCase @Inject constructor(
    private val getFilteredSongTitlesUseCase: GetFilteredSongTitlesUseCase,
) {
    suspend operator fun invoke(songFilter: SongFilter): List<Long> =
        getFilteredSongTitlesUseCase(songFilter).map { it.id }
}
