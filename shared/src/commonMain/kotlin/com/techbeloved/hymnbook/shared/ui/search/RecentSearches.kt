package com.techbeloved.hymnbook.shared.ui.search

import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class RecentSearches(
    val songs: ImmutableList<SongTitle> = persistentListOf(),
    val searches: ImmutableList<String> = persistentListOf(),
)
