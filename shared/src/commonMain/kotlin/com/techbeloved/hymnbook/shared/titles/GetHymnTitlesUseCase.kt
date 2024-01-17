package com.techbeloved.hymnbook.shared.titles

import com.techbeloved.hymnbook.shared.repository.SongRepository
import kotlinx.collections.immutable.toImmutableList

internal class GetHymnTitlesUseCase(
    private val repository: SongRepository = SongRepository(),
) {
    suspend operator fun invoke() =
        repository.allTitles().toImmutableList()
}
