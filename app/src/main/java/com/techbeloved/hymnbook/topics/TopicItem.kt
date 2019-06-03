package com.techbeloved.hymnbook.topics

import android.view.View
import com.techbeloved.hymnbook.hymnlisting.HymnItemModel

data class TopicItem(override val id: Int, override val title: String, override val subtitle: String? = null, override val description: String? = null) : HymnItemModel

interface ClickListener<T> {
    fun onItemClick(v: View, item: T)
}
