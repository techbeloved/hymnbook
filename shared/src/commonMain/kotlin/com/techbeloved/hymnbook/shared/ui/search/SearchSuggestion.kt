package com.techbeloved.hymnbook.shared.ui.search

import kotlinx.collections.immutable.ImmutableList

internal data class SearchSuggestion(
    val suggestions: ImmutableList<String>,
    val history: ImmutableList<String>,
)
