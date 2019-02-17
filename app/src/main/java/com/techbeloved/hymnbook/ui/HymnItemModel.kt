package com.techbeloved.hymnbook.ui

interface HymnItemModel {
    val id: Int
    val title: String
    val subtitle: String?
    val description: String?
}

data class TitleItem(override val id: Int,
                     override val title: String,
                     override val subtitle: String? = null,
                     override val description: String? = null): HymnItemModel