package com.techbeloved.hymnbook.shared.titles

import com.techbeloved.hymnbook.shared.model.HymnItem
import com.techbeloved.hymnbook.shared.repository.SongRepository
import kotlinx.collections.immutable.toImmutableList

internal class GetHymnTitlesUseCase(
    private val repository: SongRepository = SongRepository(),
) {
    suspend operator fun invoke() =
        repository.allTitles().map {
            HymnItem(
                id = it.id,
                title = it.title,
                subtitle = it.alternateTitle
                    ?: "${it.songbook}. No. ${it.songbookEntry}",
            )
        }.toImmutableList()
}
