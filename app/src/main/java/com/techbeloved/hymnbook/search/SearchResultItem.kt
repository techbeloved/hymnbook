package com.techbeloved.hymnbook.search

import com.techbeloved.hymnbook.hymnlisting.HymnItemModel

/**
 * Holds a single search result that can be displayed to the user
 */
data class SearchResultItem(override val id: Int,
                            override val title: String,
                            override val subtitle: String,
                            override val description: String? = null): HymnItemModel
