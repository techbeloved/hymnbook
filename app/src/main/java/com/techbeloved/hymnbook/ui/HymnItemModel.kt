package com.techbeloved.hymnbook.ui

import androidx.recyclerview.widget.DiffUtil

interface HymnItemModel {
    object diffCallback: DiffUtil.ItemCallback<HymnItemModel>() {
        override fun areItemsTheSame(oldItem: HymnItemModel, newItem: HymnItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HymnItemModel, newItem: HymnItemModel): Boolean {
            return oldItem.title == newItem.title && oldItem.subtitle == newItem.subtitle
        }

    }

    val id: Int
    val title: String
    val subtitle: String?
    val description: String?
}

data class TitleItem(override val id: Int,
                     override val title: String,
                     override val subtitle: String? = null,
                     override val description: String? = null): HymnItemModel