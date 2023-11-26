package com.techbeloved.hymnbook.shared.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import com.techbeloved.hymnbook.shared.model.HymnItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeScreenModel : ScreenModel {
    val state: StateFlow<ImmutableList<HymnItem>> = MutableStateFlow(sampleHymnItems)

    companion object {
        private val sampleHymnItems = persistentListOf(
            HymnItem(id = 1, title = "Hymn 1", subtitle = "Praise God"),
            HymnItem(id = 2, title = "Hymn 2", subtitle = "Praise waits"),
            HymnItem(id = 3, title = "Hymn 3", subtitle = "Praise is lovely"),
            HymnItem(id = 4, title = "Hymn 4", subtitle = "Praise is good"),
            HymnItem(id = 5, title = "Hymn 5", subtitle = "Praise and worship"),
            HymnItem(id = 6, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 7, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 8, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 9, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 10, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 11, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 12, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 13, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 14, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 15, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 16, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 17, title = "Hymn 6", subtitle = "Praise in holiness"),
        )
    }
}
