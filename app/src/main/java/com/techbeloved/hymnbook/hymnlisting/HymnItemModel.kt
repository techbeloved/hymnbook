package com.techbeloved.hymnbook.hymnlisting

import android.view.View
import androidx.recyclerview.widget.DiffUtil

interface HymnItemModel {

    val id: Int
    val title: String
    val subtitle: String?
    val description: String?

    object HymnItemDiffCallback: DiffUtil.ItemCallback<HymnItemModel>() {
        override fun areItemsTheSame(oldItem: HymnItemModel, newItem: HymnItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HymnItemModel, newItem: HymnItemModel): Boolean {
            return oldItem.title == newItem.title && oldItem.subtitle == newItem.subtitle
        }

    }

    interface ClickListener<T> {
        fun onItemClick(view: View, item: T)
        fun onItemLongClick(view: View, item: T): Boolean = false
    }
}

data class TitleItem(override val id: Int,
                     override val title: String,
                     override val subtitle: String? = null,
                     override val description: String? = null): HymnItemModel